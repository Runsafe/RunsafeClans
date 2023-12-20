package no.runsafe.clans.commands;

import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;

public class ClanFlare extends PlayerCommand
{
	public ClanFlare(ClanHandler handler)
	{
		super("flare", "Broadcast your location in clan chat", "runsafe.clans.flare");
		this.handler = handler;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		if (!handler.playerIsInClan(executor))
			return Config.Message.userNotInClan;

		ILocation location = executor.getLocation();
		if (location == null)
			return Config.Message.invalidLocation;

		handler.clanChat(executor, String.format(
			Config.Message.Info.assistanceRequired,
			location.getBlockX(),
			location.getBlockY(),
			location.getBlockZ()
		));
		return null;
	}

	private final ClanHandler handler;
}
