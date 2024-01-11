package no.runsafe.clans.handlers;

import no.runsafe.clans.Clan;
import no.runsafe.clans.Config;
import no.runsafe.clans.RunsafeClans;
import no.runsafe.clans.TimeFormatter;
import no.runsafe.clans.chat.ClanChannel;
import no.runsafe.clans.database.*;
import no.runsafe.clans.events.ClanEvent;
import no.runsafe.clans.events.ClanJoinEvent;
import no.runsafe.clans.events.ClanKickEvent;
import no.runsafe.clans.events.ClanLeaveEvent;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.player.IPlayerCustomEvent;
import no.runsafe.framework.api.event.player.IPlayerJoinEvent;
import no.runsafe.framework.api.event.player.IPlayerQuitEvent;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.hook.IPlayerDataProvider;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerJoinEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerQuitEvent;
import no.runsafe.nchat.channel.IChannelManager;
import no.runsafe.nchat.channel.IChatChannel;
import no.runsafe.nchat.chat.InternalRealChatEvent;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ClanHandler implements IConfigurationChanged, IPlayerDataProvider, IPlayerJoinEvent, IPlayerQuitEvent, IPlayerCustomEvent
{
	public ClanHandler(IConsole console, IScheduler scheduler, ClanRepository clanRepository,
		ClanMemberRepository memberRepository, ClanInviteRepository inviteRepository,
		ClanDergonKillRepository dergonKillRepository, ClanKillRepository killRepository, IChannelManager channelManager
	)
	{
		this.console = console;
		this.scheduler = scheduler;
		this.clanRepository = clanRepository;
		this.memberRepository = memberRepository;
		this.inviteRepository = inviteRepository;
		this.dergonKillRepository = dergonKillRepository;
		this.killRepository = killRepository;
		this.channelManager = channelManager;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration config)
	{
		clanTagFormat = config.getConfigValueAsString("chatTag");
		LoadRostersIntoCache();
		LoadInvitesIntoCache();
	}

	@Override
	public Map<String, String> GetPlayerData(IPlayer player)
	{
		Map<String, String> data = new HashMap<>(1);
		Clan playerClan = getPlayerClan(player);
		data.put("runsafe.clans.clan", playerClan == null ? "None" : playerClan.getId());
		data.put("runsafe.clans.joined", getPlayerJoinString(player));
		return data;
	}

	public String getPlayerJoinString(IPlayer player)
	{
		return TimeFormatter.formatInstant(memberRepository.getClanMemberJoinDate(player));
	}

	@Override
	public void OnPlayerCustomEvent(RunsafeCustomEvent event)
	{
		if (!(event instanceof ClanEvent))
			return;

		Clan clan = ((ClanEvent) event).getClan();
		IPlayer player = event.getPlayer();
		if (event instanceof ClanJoinEvent)
		{
			joinClanChannel(player, clan.getId());
			sendMessageToClan(clan.getId(), String.format(Config.Message.playerClanJoin, player.getPrettyName()));
		}
		else if (event instanceof ClanLeaveEvent)
		{
			leaveClanChannel(player, clan.getId());
			sendMessageToClan(clan.getId(), String.format(Config.Message.playerClanLeave, player.getPrettyName()));
		}
		else if (event instanceof ClanKickEvent)
		{
			leaveClanChannel(player, clan.getId());
			sendMessageToClan(clan.getId(), String.format(
				Config.Message.playerClanKick,
				player.getPrettyName(), ((ClanKickEvent) event).getKicker().getPrettyName())
			);
		}
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent event)
	{
		if (event.isFake())
			return;
		IPlayer player = event.getPlayer(); // Grab the player.

		// Check if we have any pending invites.
		if (playerInvites.containsKey(player))
			processPendingInvites(player);

		if (playerIsInClan(player))
			processClanMemberConnected(player);
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		if (!event.isFake() && playerIsInClan(event.getPlayer()))
			processClanMemberDisconnected(event);
	}

	public void createClan(String clanID, IPlayer playerLeader)
	{
		clanID = clanID.toUpperCase(); // Make sure the clan ID is upper-case.
		if (clanExists(clanID)) return; // Be sure we don't have a clan with this name already.
		Clan newClan = new Clan(clanID, playerLeader, String.format(Config.Message.Info.welcome, clanID)); // Create a new clan object.
		clans.put(clanID, newClan); // Push the clan into the clan handler.
		clanRepository.persistClan(newClan); // Persist the clan in the database.
	}

	public boolean isInvalidClanName(String clanID)
	{
		// Check if we have a valid name that matches the pattern.
		return !clanNamePattern.matcher(clanID).matches();
	}

	public boolean clanExists(String clanID)
	{
		return clans.containsKey(clanID); // Do we have a clan with this name?
	}

	public void addClanMember(String clanID, IPlayer player)
	{
		removeAllPendingInvites(player); // Remove all pending invites.
		Clan clan = clans.get(clanID);
		clan.addMember(player); // Add to cache.
		playerClanIndex.put(player, clanID); // Add to index.
		memberRepository.addClanMember(clan.getId(), player);
		new ClanJoinEvent(player, clan).Fire(); // Fire a join event.
	}

	public void kickClanMember(IPlayer player, IPlayer kicker)
	{
		Clan playerClan = getPlayerClan(player);

		if (playerClan == null)
			return;

		removeClanMember(playerClan, player);
		new ClanKickEvent(player, playerClan, kicker).Fire();
	}

	public void removeClanMember(IPlayer player)
	{
		Clan playerClan = getPlayerClan(player);

		if (playerClan == null)
			return;

		removeClanMember(playerClan, player);
		new ClanLeaveEvent(player, playerClan).Fire();
	}

	private void removeClanMember(Clan clan, IPlayer player)
	{
		clans.get(clan.getId()).removeMember(player); // Remove from cache.
		playerClanIndex.remove(player); // Remove from index.
		memberRepository.removeClanMember(player);
	}

	public void changeClanLeader(String clanID, IPlayer newLeader)
	{
		clans.get(clanID).setLeader(newLeader);
		clanRepository.changeClanLeader(clanID, newLeader);
		sendMessageToClan(clanID, String.format(Config.Message.newPlayerGivenClanLeadership, newLeader.getPrettyName()));
	}

	public boolean playerIsInClan(IPlayer player)
	{
		return playerClanIndex.containsKey(player);
	}

	public boolean playerIsInClan(IPlayer player, String clanID)
	{
		return playerClanIndex.containsKey(player) && playerClanIndex.get(player).equals(clanID);
	}

	public Clan getPlayerClan(IPlayer player)
	{
		return playerClanIndex.containsKey(player) ? getClan(playerClanIndex.get(player)) : null;
	}

	public Clan getClan(String clanID)
	{
		return clans.getOrDefault(clanID, null);
	}

	public boolean playerIsClanLeader(IPlayer player)
	{
		Clan playerClan = getPlayerClan(player);
		return playerClan != null && playerClan.getLeader().equals(player);
	}

	public boolean playerHasPendingInvite(String clanID, IPlayer player)
	{
		return playerInvites.containsKey(player) && playerInvites.get(player).contains(clanID);
	}

	public void invitePlayerToClan(String clanID, IPlayer player)
	{
		if (!playerInvites.containsKey(player))
			playerInvites.put(player, new ArrayList<>(1));

		playerInvites.get(player).add(clanID); // Add clan invite to the player.
		inviteRepository.addInvite(player, clanID);

		NotifyNewInvite(clanID, player);
	}

	public void removeAllPendingInvites(IPlayer player)
	{
		playerInvites.remove(player); // Remove all pending invites.
		inviteRepository.clearAllPendingInvites(player); // Persist the change in database.
	}

	public void removePendingInvite(IPlayer player, String clanName)
	{
		if (playerInvites.containsKey(player))
			playerInvites.get(player).remove(clanName);

		inviteRepository.clearPendingInvite(player, clanName);
	}

	public void acceptClanInvite(String clanID, IPlayer player)
	{
		// Make sure the player has a pending invite we can accept.
		if (!playerHasPendingInvite(clanID, player))
			return;

		addClanMember(clanID, player); // Add the member to the clan.
		Clan playerClan = getPlayerClan(player);
		if (playerClan == null)
			return;

		sendMessageOfTheDay(player, playerClan);
	}

	public void sendMessageToClan(String clanID, String message)
	{
		Clan clan = getClan(clanID); // Grab the clan.
		// Make sure said clan exists.
		if (clan == null)
			return;

		channelManager.getChannelByName(clanID).SendSystem(message);
	}

	public String formatClanMessage(String clanID, String message)
	{
		return formatClanTag(clanID) + message;
	}

	public String formatMotd(String message)
	{
		return String.format(Config.Message.Info.motd, message);
	}

	public void setClanMotd(String clanID, String message)
	{
		clans.get(clanID).setMotd(message);
		clanRepository.updateMotd(clanID, message);
		sendMessageToClan(clanID, formatMotd(message));
	}

	public void disbandClan(Clan clan)
	{
		String clanID = clan.getId();
		sendMessageToClan(clanID, Config.Message.clanDisbanded);
		PurgePendingInvites(clanID);
		PurgeMembers(clan, clanID);
		PurgeClan(clanID);
	}

	public void clanChat(IPlayer player, String message)
	{
		Clan playerClan = getPlayerClan(player);
		if (playerClan == null)
			return;

		IChatChannel channel = channelManager.getChannelByName(playerClan.getId());
		channel.Send(new InternalRealChatEvent(player, message));
	}

	public void addClanKill(IPlayer killer, IPlayer killed)
	{
		Clan killerClan = getPlayerClan(killer);
		if (killerClan != null)
		{
			killerClan.addClanKills(1);
			killerClan.addRecentClanKills(1);
			clanRepository.updateStatistic(killerClan.getId(), "clanKills", killerClan.getClanKills());
		}

		Clan killedClan = getPlayerClan(killed);
		if (killedClan != null)
		{
			killedClan.addClanDeaths(1);
			killedClan.addRecentClanDeaths(1);
			clanRepository.updateStatistic(killedClan.getId(), "clanDeaths", killedClan.getClanDeaths());
		}

		if (killerClan != null && killedClan != null)
			killRepository.recordKill(killer, killerClan.getId(), killed, killedClan.getId());
	}

	public void addDergonKill(IPlayer player)
	{
		Clan clan = getPlayerClan(player);
		String clanID = null;
		if (clan != null)
		{
			clanID = clan.getId();
			clan.addDergonKills(1);
			clan.addRecentDergonKills(1);
			clanRepository.updateStatistic(clanID, "dergonKills", clan.getDergonKills());
			RunsafeClans.server.broadcastMessage(String.format(Config.Message.Info.dergonSlay, clanID));
		}
		dergonKillRepository.recordDergonKill(player, clanID);
	}

	public String formatClanTag(String name)
	{
		return String.format(clanTagFormat, name);
	}

	public Map<String, Clan> getClans()
	{
		return clans;
	}

	public void joinClanChannel(IPlayer player, String id)
	{
		IChatChannel clanChannel = channelManager.getChannelByName(id);
		if (clanChannel == null)
		{
			clanChannel = new ClanChannel(console, channelManager, id, this);
			channelManager.registerChannel(clanChannel);
		}
		clanChannel.Join(player);
	}

	public void leaveClanChannel(IPlayer player, String id)
	{
		IChatChannel clanChannel = channelManager.getChannelByName(id);
		if (clanChannel != null)
			clanChannel.Leave(player);
	}

	private void processClanMemberDisconnected(RunsafePlayerQuitEvent event)
	{
		Clan playerClan = getPlayerClan(event.getPlayer());
		leaveClanChannel(event.getPlayer(), playerClan.getId());
	}

	private void LoadInvitesIntoCache()
	{
		playerInvites.clear();
		playerInvites.putAll(inviteRepository.getPendingInvites()); // Grab pending invites from the database.
		List<String> invalidClans = new ArrayList<>(0);

		for (Map.Entry<IPlayer, List<String>> inviteNode : playerInvites.entrySet())
		{
			for (String clanName : inviteNode.getValue()) // Loop through all the invites and check they are valid.
			{
				if (clanExists(clanName)) // Check the clan exists.
					continue;

				invalidClans.add(clanName);
				console.logError("Invalid clan invite found: %s - Marking for purge!", clanName);
			}
		}

		// Process invalid clans found in invites and purge!
		for (String invalidClan : invalidClans)
			inviteRepository.clearAllPendingInvitesForClan(invalidClan);

		for (Map.Entry<IPlayer, List<String>> inviteNode : playerInvites.entrySet())
			inviteNode.getValue().removeAll(invalidClans);
	}

	private void LoadRostersIntoCache()
	{
		int memberCount = 0; // Keep track of how many members we have.
		clans.clear();
		clans.putAll(clanRepository.getClans()); // Populate a list of clans.
		playerClanIndex.clear(); // Clear the index.
		Map<String, List<IPlayer>> rosters = memberRepository.getClanRosters(); // Get rosters.

		// Process the clan rosters into the handler.
		for (Map.Entry<String, List<IPlayer>> roster : rosters.entrySet())
		{
			String clanName = roster.getKey(); // Grab the name of the clan.
			if (!clans.containsKey(clanName))
			{
				// We have clan members for a non-existent clan, remove them.
				memberRepository.removeAllClanMembers(clanName);
				console.logError("Purging %s members from invalid clan: %s", roster.getValue().size(), clanName);
				continue;
			}

			// We have clan members, add them to the clan.
			for (IPlayer clanMember : roster.getValue())
			{
				playerClanIndex.put(clanMember, clanName); // Map the player to the clan index.
				clans.get(clanName).addMember(clanMember); // Add the member to the clan.
				memberCount++; // Increase our counter.
			}
		}

		// Output some statistics from our clan loading.
		console.logInformation("Loaded %s clans with %s members.", clans.size(), memberCount);
	}

	private void processClanMemberConnected(final IPlayer player)
	{
		final Clan playerClan = getPlayerClan(player);
		if (playerClan == null)
			return;

		scheduler.startAsyncTask(() ->
		{
			if (player.isOnline())
			{
				joinClanChannel(player, playerClan.getId());
				sendMessageOfTheDay(player, playerClan);
			}
		}, 3);
	}

	private void sendMessageOfTheDay(IPlayer player, Clan playerClan)
	{
		player.sendColouredMessage(formatClanMessage(playerClan.getId(), formatMotd(playerClan.getMotd())));
	}

	private void processPendingInvites(final IPlayer player)
	{
		final List<String> invites = playerInvites.get(player);

		if (invites.isEmpty())
			playerInvites.remove(player);
		else
			scheduler.startAsyncTask(() -> NotifyPendingInvites(player, invites), 3);
	}

	private void NotifyNewInvite(String clanID, IPlayer player)
	{
		if (player.isOnline()) // If the player is online, inform them about the invite!
			player.sendColouredMessage(Config.Message.Invite.userNotifyNew, clanID);
	}

	private void NotifyPendingInvites(IPlayer player, List<String> invites)
	{
		if (!player.isOnline())
			return;

		player.sendColouredMessage(Config.Message.Invite.userNotifyLine1, invites.size(), StringUtils.join(invites, ", "));
		player.sendColouredMessage(Config.Message.Invite.userNotifyLine2);
	}

	private void PurgeClan(String clanID)
	{
		// Delete the clan from the databases.
		clanRepository.deleteClan(clanID);
		dergonKillRepository.deleteClan(clanID);
		killRepository.deleteClan(clanID);

		// Delete the clan from the cache.
		clans.remove(clanID);
	}

	private void PurgeMembers(Clan clan, String clanID)
	{
		memberRepository.removeAllClanMembers(clanID); // Wipe the roster.
		for (IPlayer clanMember : clan.getMembers())
		{
			playerClanIndex.remove(clanMember); // Remove the players clan index.
			memberRepository.removeClanMember(clanMember);
			new ClanLeaveEvent(clanMember, clan).Fire(); // Fire a leave event.
		}
	}

	private void PurgePendingInvites(String clanID)
	{
		// Check all pending invites and remove any for this clan.
		inviteRepository.clearAllPendingInvitesForClan(clanID); // Clear all pending invites.
		for (Map.Entry<IPlayer, List<String>> invite : playerInvites.entrySet())
			if (invite.getValue().contains(clanID))
				playerInvites.get(invite.getKey()).remove(clanID); // Remove the invite from deleted clan.
	}

	public int getPlayerDergonKills(IPlayer player)
	{
		return dergonKillRepository.getDergonKills(player);
	}

	public int getPlayerClanKills(IPlayer player)
	{
		return killRepository.getPlayerKills(player);
	}

	public int getPlayerClanDeaths(IPlayer player)
	{
		return killRepository.getPlayerDeaths(player);
	}

	private String clanTagFormat;
	private final Map<String, Clan> clans = new ConcurrentHashMap<>(0);
	private final Map<IPlayer, String> playerClanIndex = new ConcurrentHashMap<>(0);
	private final Map<IPlayer, List<String>> playerInvites = new ConcurrentHashMap<>(0);
	private final IConsole console;
	private final IScheduler scheduler;
	private final ClanRepository clanRepository;
	private final ClanMemberRepository memberRepository;
	private final ClanInviteRepository inviteRepository;
	private final ClanDergonKillRepository dergonKillRepository;
	private final ClanKillRepository killRepository;
	private final Pattern clanNamePattern = Pattern.compile("^[A-Z]{3}$");
	private final IChannelManager channelManager;
}
