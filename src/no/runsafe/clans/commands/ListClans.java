package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.AsyncCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class ListClans extends AsyncCommand
{
	public ListClans(IScheduler scheduler, ClanHandler handler)
	{
		super("list", "List all clans", "runsafe.clans.list", scheduler);
		this.handler = handler;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		Map<String, Clan> clans = handler.getClans();
		return "&6Clans: (" + clans.size() + ") &r" + StringUtils.join(clans.keySet(), ", ");
	}

	private final ClanHandler handler;
}
