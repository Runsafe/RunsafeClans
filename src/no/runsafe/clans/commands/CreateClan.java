package no.runsafe.clans.commands;

import no.runsafe.clans.Config;
import no.runsafe.clans.handlers.CharterHandler;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
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
		if (!config.getClanUniverse().contains(executor.getUniverse().getName()))
			return Config.wrongWorldMessage;

		// Check for clan names that shouldn't be displayed.
		if (clanName.contains("%"))
			return Config.censoredInvalidClanTagMessage;

		// Check we have been given a valid clan name.
		if (clanHandler.isInvalidClanName(clanName))
			return String.format(Config.invalidClanTagMessage, clanName);

		// Make sure there is not a clan with that name already existing.
		if (clanHandler.clanExists(clanName))
			return String.format(Config.clanAlreadyExistsMessage, clanName);

		if (clanHandler.playerIsInClan(executor))
			return Config.userAlreadyInClanMessage;

		// Check if minimum clan size is too small for a charter to be needed.
		if (config.getMinClanSize() < 2)
		{
			clanHandler.createClan(clanName, executor); // Forge the clan!
			clanHandler.addClanMember(clanName, executor); // Add the signing player to the clan.
			return Config.clanFormMessage;
		}

		charterHandler.givePlayerCharter(executor, clanName); // Give them a charter.
		return Config.charterCreatedMessage;
	}

	private final ClanHandler clanHandler;
	private final CharterHandler charterHandler;
	private final Config config;
}
