package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.RequiredArgument;
import no.runsafe.framework.api.command.argument.TrailingArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;

public class ClanSpeak extends PlayerCommand
{
	public ClanSpeak(ClanHandler handler)
	{
		super("clanspeak", "Allows you to speak in a clan chat", "runsafe.admin.clans", new RequiredArgument("clan"), new TrailingArgument("message", true));
		this.handler = handler;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		Clan clan = handler.getClan(parameters.get("clan"));

		if (clan == null)
			return "&cInvalid clan.";

		String message = parameters.get("message");
		handler.clanChat(executor, clan, message);
		return "&3[" + clan.getId() + "] &7" + executor.getPrettyName() + "&7: " + message;
	}

	private final ClanHandler handler;
}
