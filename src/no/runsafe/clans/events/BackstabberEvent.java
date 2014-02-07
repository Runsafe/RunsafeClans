package no.runsafe.clans.events;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class BackstabberEvent extends RunsafeCustomEvent
{
	public BackstabberEvent(IPlayer player)
	{
		super(player, "runsafe.clans.backstabber");
	}

	@Override
	public Object getData()
	{
		return null;
	}
}
