package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.TrailingArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.text.ChatColour;

public class SetMotd extends PlayerAsyncCommand
{
	public SetMotd(IScheduler scheduler, ClanHandler clanHandler)
	{
		super("motd", "Change the clan message of the day", "runsafe.clans.motd", scheduler, new TrailingArgument("motd"));
		this.clanHandler = clanHandler;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		if (!clanHandler.playerIsInClan(executor))
			return "&cYou are not in a clan.";

		if (!clanHandler.playerIsClanLeader(executor))
			return "&cYou are not the clan leader.";

		Clan clan = clanHandler.getPlayerClan(executor);
		if (clan == null)
			return "&cSomething just broke.";

		clanHandler.setClanMotd(clan.getId(), ChatColour.Strip(parameters.getValue("motd")));
		return null;
	}

	private final ClanHandler clanHandler;
}
