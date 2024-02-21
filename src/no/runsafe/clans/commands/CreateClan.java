package no.runsafe.clans.commands;

import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.CharterHandler;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IUniverse;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.RequiredArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class CreateClan extends PlayerAsyncCommand
{
	public CreateClan(IScheduler scheduler, ClanHandler clanHandler, CharterHandler charterHandler, Config config)
	{
		super(
			"create",
			"Create a clan",
			"runsafe.clans.create",
			scheduler,
			new RequiredArgument("clanTag").toUppercase()
		);
		this.clanHandler = clanHandler;
		this.charterHandler = charterHandler;
		this.config = config;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		String clanName = parameters.getRequired("clanTag");

		// Make sure the player is in the right universe.
		IUniverse universe = executor.getUniverse();
		if (universe == null || !config.getClanUniverse().contains(universe.getName()))
			return Config.Message.wrongWorld;

		// Check for clan names that shouldn't be displayed.
		if (clanName.contains("%"))
			return Config.Message.censoredInvalidClanTag;

		// Check we have been given a valid clan name.
		if (clanHandler.isInvalidClanName(clanName))
			return String.format(Config.Message.invalidClanTag, clanName);

		// Make sure there is not a clan with that name already existing.
		if (clanHandler.clanExists(clanName))
			return String.format(Config.Message.clanAlreadyExists, clanName);

		if (clanHandler.playerIsInClan(executor))
			return Config.Message.userAlreadyInClan;

		// Check if we need a charter to create this clan.
		if (config.getMinClanSize() > 1)
		{
			charterHandler.givePlayerCharter(executor, clanName); // Give them a charter.
			return Config.Message.Charter.created;
		}

		clanHandler.createClan(clanName, executor); // Forge the clan!
		clanHandler.addClanMember(clanName, executor); // Add the signing player to the clan.
		return Config.Message.Charter.clanForm;
	}

	private final ClanHandler clanHandler;
	private final CharterHandler charterHandler;
	private final Config config;
}
