package no.runsafe.clans.commands;

import no.runsafe.clans.handlers.RankingHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.AsyncCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;

import java.util.List;

public class ClanRankings extends AsyncCommand
{
	public ClanRankings(IScheduler scheduler, RankingHandler rankingHandler)
	{
		super("rankings", "View the clan rankings leaderboard", "runsafe.clans.rankings", scheduler);
		this.rankingHandler = rankingHandler;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		StringBuilder data = new StringBuilder("Current Top Clan Rankings");
		List<String> roster = rankingHandler.getRankingRoster();

		int current = 1;
		for (String clan : roster)
		{
			if (current == 4) break;
			data.append(formatLine(current, clan));
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
