package no.runsafe.clans.events;

import no.runsafe.clans.Clan;
import no.runsafe.framework.api.player.IPlayer;

public class ClanJoinEvent extends ClanEvent
{
	public ClanJoinEvent(IPlayer player, Clan clan)
	{
		super(player, clan, "runsafe.clans.join");
	}
}
