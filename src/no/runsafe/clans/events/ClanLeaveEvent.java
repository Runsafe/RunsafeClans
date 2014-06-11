package no.runsafe.clans.events;

import no.runsafe.clans.Clan;
import no.runsafe.framework.api.player.IPlayer;

public class ClanLeaveEvent extends ClanEvent
{
	public ClanLeaveEvent(IPlayer player, Clan clan)
	{
		super(player, clan, "runsafe.clans.leave");
	}

	@Override
	public Object getData()
	{
		return null;
	}
}
