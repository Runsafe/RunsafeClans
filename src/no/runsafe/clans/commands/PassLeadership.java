package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class PassLeadership extends PlayerAsyncCommand
{
	public PassLeadership(IScheduler scheduler, ClanHandler clanHandler)
	{
		super("passleadership", "Make another clan member leader.", "runsafe.clans.promote", scheduler, new Player().require());
		this.clanHandler = clanHandler;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		if (!clanHandler.playerIsInClan(executor))
			return Config.userNotInClanMessage;

		if (!clanHandler.playerIsClanLeader(executor))
			return Config.userNotClanLeaderMessage;

		IPlayer targetPlayer = parameters.getValue("player") ;
		if (targetPlayer == null)
			return Config.invalidPlayerMessage;

		Clan playerClan = clanHandler.getPlayerClan(executor); // The clan of the player.
		if (!clanHandler.playerIsInClan(targetPlayer, playerClan.getId()))
			return Config.playerNotInUserClanMessage;

		clanHandler.changeClanLeader(playerClan.getId(), targetPlayer); // Change the leader.
		return Config.userPassLeadershipMessage;
	}

	private final ClanHandler clanHandler;
}
