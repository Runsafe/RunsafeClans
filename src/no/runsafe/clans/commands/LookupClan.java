package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.AsyncCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.player.IPlayer;

public class LookupClan extends AsyncCommand
{
	public LookupClan(IScheduler scheduler, ClanHandler clanHandler)
	{
		super("lookupplayer", "Lookup which clan a player is in", "runsafe.clans.lookup", scheduler, new Player().require());
		this.clanHandler = clanHandler;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		IPlayer targetPlayer = parameters.getValue("player");
		if (targetPlayer == null)
			return Config.Message.invalidPlayer;

		String returnMessage;
		if (!clanHandler.playerIsInClan(targetPlayer))
			returnMessage = Config.Message.playerNotInClan;
		else
		{
			Clan clan = clanHandler.getPlayerClan(targetPlayer);
			returnMessage = String.format(Config.Message.Info.playerLookup, targetPlayer.getPrettyName(),
				clan.getId(), clanHandler.getPlayerJoinString(targetPlayer)
			);
		}

		int dergonKills = clanHandler.getPlayerDergonKills(targetPlayer);
		int clanKills = clanHandler.getPlayerClanKills(targetPlayer);
		int clanDeaths = clanHandler.getPlayerClanDeaths(targetPlayer);
		if (dergonKills != 0 || clanKills != 0 || clanDeaths != 0)
			returnMessage += ("\n" + String.format(Config.Message.Info.playerStats, dergonKills, clanKills, clanDeaths));

		return returnMessage;
	}

	private final ClanHandler clanHandler;
}
