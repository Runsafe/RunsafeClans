package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.clans.handlers.RankingHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.AsyncCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.player.IPlayer;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ClanAllTimeRankings extends AsyncCommand
{
	public ClanAllTimeRankings(IScheduler scheduler, RankingHandler rankingHandler, ClanHandler clanHandler, Config config)
	{
		super("alltimerankings", "View the clan rankings leaderboard", "runsafe.clans.rankings", scheduler);
		this.rankingHandler = rankingHandler;
		this.clanHandler = clanHandler;
		this.config = config;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		executor.sendColouredMessage(Config.Message.Info.clanRankingsAllTimeLine1);
		List<String> roster = rankingHandler.getRankingRoster(false);

		int current = 1;
		int listLength = config.getClanRankingListLength() + 1;
		for (String clan : roster)
		{
			if (current == listLength) break;

			executor.sendComplexMessage(
				formatLine(current, clan),
				formatHoverText(clanHandler.getClan(clan)),
				null
			);
			current += 1;
		}

		return null;
	}

	private String formatLine(Object key, Object value)
	{
		return "\n" + String.format(Config.Message.Info.clanRankingsLineFormatting, key, value);
	}

	private String formatHoverText(Clan clan)
	{
		return String.format(Config.Message.Info.clanRankingsHoverText,
			clan.getDergonKills(),
			clan.getClanKills(),
			clan.getClanDeaths(),
			StringUtils.join(clan.getMembers().stream().map(IPlayer::getPrettyName).collect(Collectors.toList()), ", ")
		);
	}

	private final RankingHandler rankingHandler;
	private final ClanHandler clanHandler;
	private final Config config;
}
