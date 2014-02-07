package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.AnyPlayerRequired;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class PassLeadership extends PlayerAsyncCommand
{
	public PassLeadership(IScheduler scheduler, ClanHandler clanHandler)
	{
		super("passleadership", "Pass the leadership of your clan to another", "runsafe.clans.promote", scheduler, new AnyPlayerRequired());
		this.clanHandler = clanHandler;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		String playerName = executor.getName();
		if (!clanHandler.playerIsInClan(playerName))
			return "&cYou are not in a clan.";

		if (!clanHandler.playerIsClanLeader(playerName))
			return "&cYou are not the leader of your clan.";

		IPlayer targetPlayer = parameters.getPlayer("player");
		if (targetPlayer == null)
			return "&cInvalid player";

		String targetPlayerName = targetPlayer.getName();

		Clan playerClan = clanHandler.getPlayerClan(playerName); // The clan of the player.
		if (!clanHandler.playerIsInClan(targetPlayerName, playerClan.getId()))
			return "&cThat player is not in your clan.";

		clanHandler.changeClanLeader(playerClan.getId(), targetPlayer); // Change the leader.
		return "&aYou have passed the leadership of your clan!";
	}

	private final ClanHandler clanHandler;
}
