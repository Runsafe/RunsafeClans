package no.runsafe.clans;

import no.runsafe.clans.commands.*;
import no.runsafe.clans.database.ClanInviteRepository;
import no.runsafe.clans.database.ClanMemberRepository;
import no.runsafe.clans.database.ClanRepository;
import no.runsafe.clans.handlers.CharterHandler;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.clans.monitors.PlayerMonitor;
import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.api.command.Command;
import no.runsafe.framework.features.Commands;
import no.runsafe.framework.features.Database;
import no.runsafe.framework.features.Events;
import no.runsafe.framework.features.FrameworkHooks;

public class RunsafeClans extends RunsafeConfigurablePlugin
{
	@Override
	protected void pluginSetup()
	{
		// Framework features
		addComponent(Commands.class);
		addComponent(Database.class);
		addComponent(Events.class);
		addComponent(FrameworkHooks.class);

		// Plugin components

		// Database
		addComponent(ClanRepository.class);
		addComponent(ClanMemberRepository.class);
		addComponent(ClanInviteRepository.class);

		// Handlers/Monitors
		addComponent(CharterHandler.class);
		addComponent(ClanHandler.class);
		addComponent(PlayerMonitor.class);

		// Commands
		Command clans = new Command("clan", "Clan related commands", null);
		addComponent(clans);

		clans.addSubCommand(getInstance(CreateClan.class));
		clans.addSubCommand(getInstance(InviteMember.class));
		clans.addSubCommand(getInstance(ClanInfo.class));
		clans.addSubCommand(getInstance(JoinClan.class));
		clans.addSubCommand(getInstance(DisbandClan.class));
		clans.addSubCommand(getInstance(LeaveClan.class));
		clans.addSubCommand(getInstance(PassLeadership.class));
		clans.addSubCommand(getInstance(KickClanMember.class));
		clans.addSubCommand(getInstance(LookupClan.class));
		clans.addSubCommand(getInstance(SetMotd.class));

		addComponent(ClanChat.class);

	}
}
