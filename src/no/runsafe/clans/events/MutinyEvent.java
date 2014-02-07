package no.runsafe.clans.events;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class MutinyEvent extends RunsafeCustomEvent
{
	public MutinyEvent(IPlayer player)
	{
		super(player, "runsafe.clans.mutiny");
	}

	@Override
	public Object getData()
	{
		return null;
	}
}
