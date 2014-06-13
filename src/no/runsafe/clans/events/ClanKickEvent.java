package no.runsafe.clans.events;

import no.runsafe.clans.Clan;
import no.runsafe.framework.api.player.IPlayer;

public class ClanKickEvent extends ClanEvent
{
	public ClanKickEvent(IPlayer player, Clan clan, IPlayer kicker)
	{
		super(player, clan, "runsafe.clans.kick");
		this.kicker = kicker;
	}

	public IPlayer getKicker()
	{
		return kicker;
	}

	private final IPlayer kicker;
}
