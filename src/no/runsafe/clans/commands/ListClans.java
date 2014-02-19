package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class ListClans extends PlayerAsyncCommand
{
	public ListClans(IScheduler scheduler, ClanHandler handler)
	{
		super("list", "List all clans", "runsafe.clans.list", scheduler);
		this.handler = handler;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		Map<String, Clan> clans = handler.getClans();
		return "&6Clans: (" + clans.size() + ") &r" + StringUtils.join(clans.keySet(), ", ");
	}

	private final ClanHandler handler;
}
