package no.runsafe.clans.handlers;

import no.runsafe.clans.Clan;
import no.runsafe.clans.Config;

import java.util.*;

public class RankingHandler
{
	public RankingHandler(ClanHandler clanHandler, Config config)
	{
		this.clanHandler = clanHandler;
		this.config = config;
	}

	public List<String> getRankingRoster()
	{
		Map<String, Clan> clanMap = clanHandler.getClans();
		Map<String, Integer> roster = new HashMap<String, Integer>(clanMap.size());

		for (Map.Entry<String, Clan> clanNode : clanMap.entrySet())
		{
			Clan clan = clanNode.getValue();
			int score = (clan.getMemberCount() * config.getClanMemberScore())
				+ (clan.getClanKills() * config.getClanKillScore())
				- (clan.getClanDeaths() * (config.getClanKillScore() / 2))
				+ (clan.getDergonKills() * config.getClanDergonKillScore());
			roster.put(clan.getId(), score);
		}

		LinkedHashMap<String, Integer> sorted = sortByValues(roster); // Sort the stuff!
		List<String> ordered = new ArrayList<String>(sorted.size());

		List<Map.Entry<String, Integer>> holder = new ArrayList<Map.Entry<String, Integer>>(sorted.entrySet());
		for (int i = holder.size() - 1; i >= 0; i--)
			ordered.add(holder.get(i).getKey());

		return ordered;
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

	private final Config config;
	private final ClanHandler clanHandler;
}
