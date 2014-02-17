package no.runsafe.clans.commands;

import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.TrailingArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.text.ChatColour;

public class ClanChat extends PlayerAsyncCommand
{
	public ClanChat(IScheduler scheduler, ClanHandler clanHandler)
	{
		super("clanchat", "Send a message in clan chat", "runsafe.clans.chat", scheduler, new TrailingArgument("message"));
		this.clanHandler = clanHandler;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		String playerName = executor.getName();

		if (!clanHandler.playerIsInClan(playerName))
			return "&cYou are not in a clan.";

		clanHandler.clanChat(executor, ChatColour.Strip(parameters.get("message")));
		return null;
	}

	private final ClanHandler clanHandler;
}
