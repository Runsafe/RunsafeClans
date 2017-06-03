package no.runsafe.clans.commands;

import com.google.common.collect.Lists;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.ITabComplete;
import no.runsafe.framework.api.command.argument.IValueExpander;
import no.runsafe.framework.api.command.argument.RequiredArgument;
import no.runsafe.framework.api.player.IPlayer;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClanArgument extends RequiredArgument implements ITabComplete, IValueExpander
{
	public ClanArgument(ClanHandler clanHandler)
	{
		super("clan");
		this.clans = clanHandler.getClans().keySet();
	}

	public ClanArgument(String name, ClanHandler clanHandler)
	{
		super(name);
		this.clans = clanHandler.getClans().keySet();
	}

	@Override
	public List<String> getAlternatives(IPlayer executor, String partial)
	{
		return Lists.newArrayList(clans);
	}

	@Nullable
	@Override
	public String expand(ICommandExecutor context, @Nullable String value)
	{
		if (value == null)
			return null;
		for (String alternative : clans)
			if (alternative.toUpperCase().startsWith(value.toUpperCase()))
				return alternative;

		return null;
	}

	@Override
	public String getValue(IPlayer context, Map<String, String> params)
	{
		String value = params.get(name);
		if (value != null && !value.isEmpty())
			return value.toUpperCase();
		return "";
	}

	private  Set<String> clans = new HashSet<>();
}
