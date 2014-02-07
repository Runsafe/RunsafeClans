package no.runsafe.clans.commands;

import no.runsafe.clans.handlers.RankingHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClanRankings extends PlayerAsyncCommand
{
	public ClanRankings(IScheduler scheduler, RankingHandler rankingHandler)
	{
		super("rankings", "View the clan rankings leaderboard", "runsafe.clans.rankings", scheduler);
		this.rankingHandler = rankingHandler;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		StringBuilder data = new StringBuilder("Current Top Clan Rankings");
		LinkedHashMap<String, Integer> roster = rankingHandler.getRankingRoster();

		int current = 1;
		for (Map.Entry<String, Integer> node : roster.entrySet())
		{
			if (current == 4) break;
			data.append(formatLine(current, node.getKey()));
			current += 1;
		}

		return data.toString();
	}

	private String formatLine(Object key, Object value)
	{
		return "\n- &6" + key + ": &r" + value;
	}

	private final RankingHandler rankingHandler;
}