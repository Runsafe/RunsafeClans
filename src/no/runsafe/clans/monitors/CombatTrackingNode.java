package no.runsafe.clans.monitors;

public class CombatTrackingNode
{
	public String getAttacker()
	{
		return attacker;
	}

	public CombatTrackingNode setAttacker(String attacker)
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

	private String attacker;
	private int timerID;
}
