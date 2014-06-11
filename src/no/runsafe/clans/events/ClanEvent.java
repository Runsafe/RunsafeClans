package no.runsafe.clans.events;

import no.runsafe.clans.Clan;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class ClanEvent extends RunsafeCustomEvent
{
	protected ClanEvent(IPlayer player, Clan clan, String event)
	{
		super(player, event);
		this.clan = clan;
	}

	public Clan getClan()
	{
		return clan;
	}

	@Override
	public Object getData()
	{
		return null;
	}

	private final Clan clan;
}
