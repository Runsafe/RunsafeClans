package no.runsafe.clans.commands;

import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.AnyPlayerRequired;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class InviteMember extends PlayerAsyncCommand
{
	public InviteMember(IScheduler scheduler, ClanHandler clanHandler)
	{
		super("invite", "Invite a member to the clan", "runsafe.clans.invite", scheduler, new AnyPlayerRequired());
		this.clanHandler = clanHandler;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		String playerName = executor.getName();

		if (!clanHandler.playerIsInClan(playerName))
			return "&cYou are not in a clan.";

		if (!clanHandler.playerIsClanLeader(playerName))
			return "&cYou are not the clan leader, you cannot invite players.";

		IPlayer targetPlayer = parameters.getPlayer("player");
		if (targetPlayer == null)
			return "&cInvalid player.";

		String targetPlayerName = targetPlayer.getName();

		if (clanHandler.playerIsInClan(targetPlayerName))
			return "&cThat player is already in a clan.";

		// ToDo: Implement clan inviting.
		return "&aComing Soon.";
	}

	private final ClanHandler clanHandler;
}