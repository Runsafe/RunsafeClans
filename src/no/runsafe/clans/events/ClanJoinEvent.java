package no.runsafe.clans.events;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class ClanJoinEvent extends RunsafeCustomEvent
{
	public ClanJoinEvent(IPlayer player)
	{
		super(player, "runsafe.clans.join");
	}

	@Override
	public Object getData()
	{
		return null;
	}
}
