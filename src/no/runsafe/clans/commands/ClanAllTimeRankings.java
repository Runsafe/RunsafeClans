package no.runsafe.clans.commands;

import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.RankingHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.AsyncCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;

import java.util.List;

public class ClanAllTimeRankings extends AsyncCommand
{
	public ClanAllTimeRankings(IScheduler scheduler, RankingHandler rankingHandler, Config config)
	{
		super("alltimerankings", "View the clan rankings leaderboard", "runsafe.clans.rankings", scheduler);
		this.rankingHandler = rankingHandler;
		this.config = config;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		StringBuilder data = new StringBuilder(Config.Message.Info.clanRankingsAllTimeLine1);
		List<String> roster = rankingHandler.getRankingRoster(false);

		int current = 1;
		int listLength = config.getClanRankingListLength() + 1;
		for (String clan : roster)
		{
			if (current == listLength) break;
			data.append(formatLine(current, clan));
			current += 1;
		}

		return data.toString();
	}

	private String formatLine(Object key, Object value)
	{
		return String.format(Config.Message.Info.clanRankingsLineFormatting, key, value);
	}

	private final RankingHandler rankingHandler;
	private final Config config;
}
