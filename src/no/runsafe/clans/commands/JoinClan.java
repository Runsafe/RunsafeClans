package no.runsafe.clans.commands;

import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.RequiredArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class JoinClan extends PlayerAsyncCommand
{
	public JoinClan(IScheduler scheduler, ClanHandler clanHandler)
	{
		super("join", "Accept an invite to a clan", "runsafe.clans.join", scheduler, new RequiredArgument("clan"));
		this.clanHandler = clanHandler;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		String clanName = parameters.get("clan").toUpperCase();
		if (clanHandler.playerHasPendingInvite(clanName, executor.getName()))
		{
			clanHandler.acceptClanInvite(clanName, executor);
			return "&aYou have accepted the clan invite!";
		}
		return "&cYou have not been invited to that clan.";
	}

	private final ClanHandler clanHandler;
}