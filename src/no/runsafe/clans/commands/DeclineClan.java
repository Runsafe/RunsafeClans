package no.runsafe.clans.commands;

import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class DeclineClan extends PlayerAsyncCommand
{
	public DeclineClan(IScheduler scheduler, ClanHandler clanHandler)
	{
		super(
			"decline",
			"Decline a clan invitation",
			"runsafe.clans.decline",
			scheduler,
			new ClanArgument(CLAN, clanHandler)
		);
		this.clanHandler = clanHandler;
	}

	private static final String CLAN = "clan";

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		String clanName = parameters.getValue(CLAN);
		if (!clanHandler.playerHasPendingInvite(clanName, executor))
			return Config.userNotInvitedMessage;

		clanHandler.removePendingInvite(executor, clanName);
		return String.format(Config.invitationDeclinedMessage, clanName);
	}

	private final ClanHandler clanHandler;
}
