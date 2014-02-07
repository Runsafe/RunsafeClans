package no.runsafe.clans.handlers;

import no.runsafe.clans.Clan;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

import java.util.*;

public class RankingHandler implements IConfigurationChanged
{
	public RankingHandler(ClanHandler clanHandler)
	{
		this.clanHandler = clanHandler;
	}

	public LinkedHashMap<String, Integer> getRankingRoster()
	{
		Map<String, Clan> clanMap = clanHandler.getClans();
		Map<String, Integer> roster = new HashMap<String, Integer>(clanMap.size());

		for (Map.Entry<String, Clan> clanNode : clanMap.entrySet())
		{
			Clan clan = clanNode.getValue();
			int score = (clan.getMemberCount() * clanMemberScore) + (clan.getClanKills() * clanKillScore);
			roster.put(clan.getId(), score);
		}

		return sortByValues(roster);
	}

	public static <K extends Comparable,V extends Comparable> LinkedHashMap<K,V> sortByValues(Map<K,V> map)
	{
		List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K,V>>()
		{
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		LinkedHashMap<K,V> sortedMap = new LinkedHashMap<K,V>();

		for(Map.Entry<K,V> entry: entries)
			sortedMap.put(entry.getKey(), entry.getValue());

		return sortedMap;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration config)
	{
		clanMemberScore = config.getConfigValueAsInt("ranking.clanMember");
		clanKillScore = config.getConfigValueAsInt("ranking.clanKill");
		clanDergonKillScore = config.getConfigValueAsInt("ranking.dergonKill");
	}

	private int clanMemberScore;
	private int clanKillScore;
	private int clanDergonKillScore;
	private final ClanHandler clanHandler;
}
