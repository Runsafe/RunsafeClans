package no.runsafe.clans.handlers;

import no.runsafe.clans.Clan;
import no.runsafe.clans.database.ClanInviteRepository;
import no.runsafe.clans.database.ClanMemberRepository;
import no.runsafe.clans.database.ClanRepository;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.event.player.IPlayerJoinEvent;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.hook.IPlayerDataProvider;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerJoinEvent;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ClanHandler implements IConfigurationChanged, IPlayerDataProvider, IPlayerJoinEvent
{
	public ClanHandler(IConsole console, IServer server, IScheduler scheduler, ClanRepository clanRepository, ClanMemberRepository memberRepository, ClanInviteRepository inviteRepository)
	{
		this.console = console;
		this.server = server;
		this.scheduler = scheduler;
		this.clanRepository = clanRepository;
		this.memberRepository = memberRepository;
		this.inviteRepository = inviteRepository;
	}

	public void createClan(String clanID, String playerLeader)
	{
		clanID = clanID.toUpperCase(); // Make sure the clan ID is upper-case.
		if (clanExists(clanID)) return; // Be sure we don't have a clan with this name already.
		Clan newClan = new Clan(clanID, playerLeader, "Welcome to " + clanID); // Create a new clan object.
		clans.put(clanID, newClan); // Push the clan into the clan handler.
		clanRepository.persistClan(newClan); // Persist the clan in the database.
	}

	public boolean isValidClanName(String clanID)
	{
		// Check if we have a valid name that matches the pattern.
		return clanNamePattern.matcher(clanID).matches();
	}

	public boolean clanExists(String clanID)
	{
		return clans.containsKey(clanID); // Do we have a clan with this name?
	}

	public void addClanMember(String clanID, String playerName)
	{
		clans.get(clanID).addMember(playerName); // Add to cache.
		playerClanIndex.put(playerName, clanID); // Add to index.
		memberRepository.addClanMember(clanID, playerName); // Add to member database.
	}

	public void removeClanMember(String playerName, boolean kick)
	{
		Clan playerClan = getPlayerClan(playerName);

		if (playerClan != null)
		{
			String clanID = playerClan.getId();
			removeClanMember(clanID, playerName);

			sendMessageToClan(clanID, playerName + " has " + (kick ? "been kicked from" : "left") + " the clan.");
		}
	}

	private void removeClanMember(String clanID, String playerName)
	{
		clans.get(clanID).removeMember(playerName); // Remove from cache.
		memberRepository.removeClanMemberByName(playerName); // Remove from database.
		playerClanIndex.remove(playerName); // Remove from index.
	}

	public void changeClanLeader(String clanID, IPlayer newLeader)
	{
		String playerName = newLeader.getName();
		clans.get(clanID).setLeader(playerName);
		clanRepository.changeClanLeader(clanID, newLeader);
		sendMessageToClan(clanID, playerName + " has been given leadership of the clan.");
	}

	public boolean playerIsInClan(String playerName)
	{
		return playerClanIndex.containsKey(playerName);
	}

	public boolean playerIsInClan(String playerName, String clanID)
	{
		return playerClanIndex.containsKey(playerName) && playerClanIndex.get(playerName).equals(clanID);
	}

	public Clan getPlayerClan(String playerName)
	{
		return playerClanIndex.containsKey(playerName) ? getClan(playerClanIndex.get(playerName)) : null;
	}

	public Clan getClan(String clanID)
	{
		return clans.containsKey(clanID) ? clans.get(clanID) : null;
	}

	public boolean playerIsClanLeader(String playerName)
	{
		Clan playerClan = getPlayerClan(playerName);
		return playerClan != null && playerClan.getLeader().equals(playerName);
	}

	@Override
	public void OnConfigurationChanged(IConfiguration config)
	{
		int memberCount = 0; // Keep track of how many members we have.
		clans = clanRepository.getClans(); // Populate a list of clans.
		playerClanIndex.clear(); // Clear the index.
		Map<String, List<String>> rosters = memberRepository.getClanRosters(); // Get rosters.

		// Process the clan rosters into the handler.
		for (Map.Entry<String, List<String>> roster : rosters.entrySet())
		{
			String clanName = roster.getKey(); // Grab the name of the clan.
			if (clans.containsKey(clanName))
			{
				// We have clan members, add them to the clan.
				for (String clanMember : roster.getValue())
				{
					playerClanIndex.put(clanMember, clanName); // Map the player to the clan index.
					clans.get(clanName).addMember(clanMember); // Add the member to the clan.
					memberCount++; // Increase our counter.
				}
			}
			else
			{
				// We have clan members for a non-existent clan, remove them.
				memberRepository.removeAllClanMembers(clanName);
				console.logError("Purging %s members from invalid clan: %s", roster.getValue().size(), clanName);
			}
		}

		// Output some statistics from our clan loading.
		console.logInformation("Loaded %s clans with %s members.", clans.size(), memberCount);

		playerInvites = inviteRepository.getPendingInvites(); // Grab pending invites from the database.
		for (Map.Entry<String, List<String>> inviteNode : playerInvites.entrySet())
		{
			String playerName = inviteNode.getKey(); // The name of the player who's been invited.

			for (String clanName : inviteNode.getValue()) // Loop through all the invites and check they are valid.
			{
				if (!clanExists(clanName)) // Check the clan exists.
				{
					playerInvites.get(playerName).remove(clanName); // Remove non-existent clan invite.
					console.logError("Purging invalid clan invite to %s from %s", playerName, clanName);
				}
			}
		}
	}

	@Override
	public Map<String, String> GetPlayerData(IPlayer player)
	{
		Map<String, String> data = new HashMap<String, String>(1);
		Clan playerClan = getPlayerClan(player.getName());
		data.put("runsafe.clans.clan", playerClan == null ? "None" : playerClan.getId());
		data.put("runsafe.clans.joined", getPlayerJoinString(player));
		return data;
	}

	public String getPlayerJoinString(IPlayer player)
	{
		return formatTime(memberRepository.getClanMemberJoinDate(player));
	}

	private String formatTime(DateTime time)
	{
		if (time == null)
			return "null";

		Period period = new Period(time, DateTime.now(), output_format);
		return PeriodFormat.getDefault().print(period);
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent event)
	{
		final IPlayer player = event.getPlayer(); // Grab the player.
		String playerName = player.getName();

		// Check if we have any pending invites.
		if (playerInvites.containsKey(playerName))
		{
			final List<String> invites = playerInvites.get(playerName);

			scheduler.startAsyncTask(new Runnable()
			{
				@Override
				public void run()
				{
					if (player.isOnline())
					{
						player.sendColouredMessage("&aYou have %s pending clan invite(s): %s", invites.size(), StringUtils.join(invites, ", "));
						player.sendColouredMessage("&aUse \"/clan join <clanTag>\" to join one of them!");
					}
				}
			}, 3);
		}

		if (playerIsInClan(playerName))
		{
			final Clan playerClan = getPlayerClan(playerName);
			if (playerClan != null)
			{
				scheduler.startAsyncTask(new Runnable()
				{
					@Override
					public void run()
					{
						if (player.isOnline())
							sendClanMessage(playerClan.getId(), player, "Message of the Day: " + playerClan.getMotd());
					}
				}, 3);
			}
		}
	}

	public boolean playerHasPendingInvite(String clanID, String playerName)
	{
		return playerInvites.containsKey(playerName) && playerInvites.get(playerName).contains(clanID);
	}

	public void invitePlayerToClan(String clanID, IPlayer player)
	{
		String playerName = player.getName();
		if (!playerInvites.containsKey(playerName))
			playerInvites.put(playerName, new ArrayList<String>(1));

		playerInvites.get(playerName).add(clanID); // Add clan invite to the player.

		if (player.isOnline()) // If the player is online, inform them about the invite!
			player.sendColouredMessage("&aYou have been invited to join the '%1$s' clan. Use \"/clan join %1$s\" to join!", clanID);
	}

	public void removeAllPendingInvites(String playerName)
	{
		playerInvites.remove(playerName); // Remove all pending invites.
		inviteRepository.clearAllPendingInvites(playerName); // Persist the change in database.
	}

	public void acceptClanInvite(String clanID, IPlayer player)
	{
		String playerName = player.getName();

		// Make sure the player has a pending invite we can accept.
		if (playerHasPendingInvite(clanID, playerName))
		{
			removeAllPendingInvites(playerName); // Remove all pending invites.
			addClanMember(clanID, playerName); // Add the member to the clan.
			sendMessageToClan(clanID, playerName + " has joined the clan.");
		}
	}

	public void sendMessageToClan(String clanID, String message)
	{
		Clan clan = getClan(clanID); // Grab the clan.

		// Make sure said clan exists.
		if (clan != null)
		{
			// Loop all clan members.
			for (String playerName : clan.getMembers())
			{
				IPlayer player = server.getPlayerExact(playerName);
				if (player != null && player.isOnline()) // Check player is valid and online.
					sendClanMessage(clanID, player, message);
			}

			console.logInformation(message);
		}
	}

	public void sendClanMessage(String clanID, IPlayer player, String message)
	{
		player.sendColouredMessage("&3[" + clanID + "] &7" + message);
	}

	public void setClanMotd(String clanID, String message)
	{
		clans.get(clanID).setMotd(message);
		clanRepository.updateMotd(clanID, message);
		sendMessageToClan(clanID, "Message of the Day: " + message);
	}

	public void disbandClan(Clan clan)
	{
		String clanID = clan.getId();
		sendMessageToClan(clanID, "The clan has been disbanded by the leader.");

		// Loop all members.
		for (String clanMember : clan.getMembers())
			playerClanIndex.remove(clanMember); // Remove the players clan index.

		// Check all pending invites and remove any for this clan.
		for (Map.Entry<String, List<String>> invite : playerInvites.entrySet())
			if (invite.getValue().contains(clanID))
				playerInvites.get(invite.getKey()).remove(clanID); // Remove the invite from deleted clan.

		memberRepository.removeAllClanMembers(clanID); // Wipe the roster.
		clanRepository.deleteClan(clanID); // Delete the clan from the database.
		clans.remove(clanID); // Delete the clan from the cache.
		inviteRepository.clearAllPendingInvitesForClan(clanID); // Clear all pending invites.
	}

	private Map<String, Clan> clans = new ConcurrentHashMap<String, Clan>(0);
	private Map<String, String> playerClanIndex = new ConcurrentHashMap<String, String>(0);
	private Map<String, List<String>> playerInvites = new ConcurrentHashMap<String, List<String>>(0);
	private final IConsole console;
	private final IServer server;
	private final IScheduler scheduler;
	private final ClanRepository clanRepository;
	private final ClanMemberRepository memberRepository;
	private final ClanInviteRepository inviteRepository;
	private final Pattern clanNamePattern = Pattern.compile("^[A-Z]{3}$");
	private final PeriodType output_format = PeriodType.standard().withMillisRemoved().withSecondsRemoved();
}
