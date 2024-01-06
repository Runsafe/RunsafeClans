package no.runsafe.clans.commands;

import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class DisbandClan extends PlayerAsyncCommand
{
	public DisbandClan(IScheduler scheduler, ClanHandler clanHandler)
	{
		super("disband", "Disband your clan", "runsafe.clans.disband", scheduler);
		this.clanHandler = clanHandler;
	}

	@Override
	public String OnAsyncExecute(IPlayer player, IArgumentList parameters)
	{
		if (!clanHandler.playerIsInClan(player))
			return Config.Message.userNotInClan;

		if (!clanHandler.playerIsClanLeader(player))
			return Config.Message.userNotClanLeader;

		clanHandler.disbandClan(clanHandler.getPlayerClan(player));
		return Config.Message.userClanDisbanded;
	}

	private final ClanHandler clanHandler;
}
