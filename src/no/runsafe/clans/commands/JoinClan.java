package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class JoinClan extends PlayerAsyncCommand
{
	public JoinClan(IScheduler scheduler, ClanHandler clanHandler, Config config)
	{
		super(
			"join",
			"Accept an invite to a clan",
			"runsafe.clans.join",
			scheduler,
			new ClanArgument(CLAN, clanHandler)
		);
		this.clanHandler = clanHandler;
		this.config = config;
	}

	private static final String CLAN = "clan";

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		String clanName = parameters.getValue(CLAN);
		if (!clanHandler.playerHasPendingInvite(clanName, executor))
			return Config.userNotInvitedMessage;

		Clan clan = clanHandler.getClan(clanName);
		if (clan.getMemberCount() >= config.getClanSize())
			return Config.joinFailClanFullMessage;

		clanHandler.acceptClanInvite(clanName, executor);
		return Config.userAcceptInviteMessage;
	}

	private final ClanHandler clanHandler;
	private final Config config;
}