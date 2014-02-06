package no.runsafe.clans.handlers;

import no.runsafe.clans.Clan;
import no.runsafe.clans.database.ClanMemberRepository;
import no.runsafe.clans.database.ClanRepository;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.hook.IPlayerDataProvider;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ClanHandler implements IConfigurationChanged, IPlayerDataProvider
{
	public ClanHandler(IConsole console, ClanRepository clanRepository, ClanMemberRepository memberRepository)
	{
		this.console = console;
		this.clanRepository = clanRepository;
		this.memberRepository = memberRepository;
	}

	public void createClan(String clanID, String playerLeader)
	{
		clanID = clanID.toUpperCase(); // Make sure the clan ID is upper-case.
		if (clanExists(clanID)) return; // Be sure we don't have a clan with this name already.
		Clan newClan = new Clan(clanID, playerLeader); // Create a new clan object.
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
		clans.get(clanID).addMember(playerName);
		playerClanIndex.put(playerName, clanID);
		memberRepository.addClanMember(clanID, playerName);
	}

	public boolean playerIsInClan(String playerName)
	{
		return playerClanIndex.containsKey(playerName);
	}

	public Clan getPlayerClan(String playerName)
	{
		return playerClanIndex.containsKey(playerName) ? clans.get(playerClanIndex.get(playerName)) : null;
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
	}

	@Override
	public Map<String, String> GetPlayerData(IPlayer player)
	{
		Map<String, String> data = new HashMap<String, String>(1);
		Clan playerClan = getPlayerClan(player.getName());
		data.put("runsafe.clans.clan", playerClan == null ? "None" : playerClan.getId());
		return data;
	}

	private Map<String, Clan> clans = new ConcurrentHashMap<String, Clan>(0);
	private Map<String, String> playerClanIndex = new ConcurrentHashMap<String, String>(0);
	private final IConsole console;
	private final ClanRepository clanRepository;
	private final ClanMemberRepository memberRepository;
	private final Pattern clanNamePattern = Pattern.compile("^[A-Z]{3}$");
}
