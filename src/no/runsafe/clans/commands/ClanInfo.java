package no.runsafe.clans.commands;

import no.runsafe.clans.Clan;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.RequiredArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import org.apache.commons.lang.StringUtils;

public class ClanInfo extends PlayerAsyncCommand
{
	public ClanInfo(IScheduler scheduler, ClanHandler clanHandler)
	{
		super("info", "Get information on another clan.", "runsafe.clans.info", scheduler, new RequiredArgument("clan"));
		this.clanHandler = clanHandler;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		String clanName = parameters.get("clan").toUpperCase();

		if (!clanHandler.clanExists(clanName))
			return "&cNo clan named '" + clanName + "' exists.";

		StringBuilder info = new StringBuilder("Clan information:");
		Clan clan = clanHandler.getClan(clanName); // Grab the clan information

		info.append(formatLine("Name", clanName));
		info.append(formatLine("Leader", clan.getLeader()));
		info.append(formatLine("Members", "(" + clan.getMemberCount() + ") " + StringUtils.join(clan.getMembers(), ", ")));

		return info.toString();
	}

	private String formatLine(String key, Object value)
	{
		return "\n- &6" + key + ": &r" + value;
	}

	private final ClanHandler clanHandler;
}
