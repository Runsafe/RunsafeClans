package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class InviteMember extends PlayerAsyncCommand
{
	public InviteMember(IScheduler scheduler, ClanHandler clanHandler, Config config)
	{
		super("invite", "Invite a member to the clan", "runsafe.clans.invite", scheduler, new Player().require());
		this.clanHandler = clanHandler;
		this.config = config;
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

		if (clanHandler.playerIsInClan(targetPlayer))
			return Config.playerAlreadyInClanMessage;

		Clan clan = clanHandler.getPlayerClan(executor); // Grab the players clan.

		if (clan.getMemberCount() >= config.getClanSize())
			return Config.inviteFailClanFullMessage;

		if (clanHandler.playerHasPendingInvite(clan.getId(), targetPlayer))
			return Config.playerAlreadyInvitedMessage;

		clanHandler.invitePlayerToClan(clan.getId(), targetPlayer); // Invite the player.
		return Config.inviteSentMessage;
	}

	private final ClanHandler clanHandler;
	private final Config config;
}