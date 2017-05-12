package no.runsafe.clans.monitors;

import no.runsafe.framework.api.player.IPlayer;

public class CombatTrackingNode
{
	public IPlayer getAttacker()
	{
		return attacker;
	}

	public CombatTrackingNode setAttacker(IPlayer attacker)
	{
		this.attacker = attacker;
		return this;
	}

	public int getTimerID()
	{
		return timerID;
	}

	public CombatTrackingNode setTimerID(int timerID)
	{
		this.timerID = timerID;
		return this;
	}

	private IPlayer attacker;
	private int timerID;
}
