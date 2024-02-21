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
		if (clanHandler.isNotInAnyClan(executor))
			return Config.Message.userNotInClan;

		if (!clanHandler.playerIsClanLeader(executor))
			return Config.Message.userNotClanLeader;

		IPlayer targetPlayer = parameters.getValue("player") ;
		if (targetPlayer == null)
			return Config.Message.invalidPlayer;

		if (!clanHandler.isNotInAnyClan(targetPlayer))
			return Config.Message.playerAlreadyInClan;

		Clan clan = clanHandler.getPlayerClan(executor); // Grab the players clan.

		if (clan.getMemberCount() >= config.getClanSize())
			return Config.Message.Invite.failClanFull;

		if (clanHandler.playerHasPendingInvite(clan.getId(), targetPlayer))
			return Config.Message.Invite.playerAlreadyInvited;

		clanHandler.invitePlayerToClan(clan.getId(), targetPlayer); // Invite the player.
		return Config.Message.Invite.sent;
	}

	private final ClanHandler clanHandler;
	private final Config config;
}