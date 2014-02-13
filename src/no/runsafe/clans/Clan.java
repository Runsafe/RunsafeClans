package no.runsafe.clans;

import java.util.ArrayList;
import java.util.List;

public class Clan
{
	public Clan(String id, String leader, String motd)
	{
		this.id = id;
		this.leader = leader;
		this.motd = motd;
	}

	public String getMotd()
	{
		return motd;
	}

	public void setMotd(String motd)
	{
		this.motd = motd;
	}

	public void setLeader(String leader)
	{
		this.leader = leader;
	}

	public String getId()
	{
		return id;
	}

	public String getLeader()
	{
		return leader;
	}

	public List<String> getMembers()
	{
		return members;
	}

	public int getMemberCount()
	{
		return members.size();
	}

	public void addMember(String playerName)
	{
		members.add(playerName);
	}

	public void removeMember(String playerName)
	{
		members.remove(playerName);
	}

	public void addClanKills(int amount)
	{
		clanKills += amount;
	}

	public void addClanDeaths(int amount)
	{
		clanDeaths += amount;
	}

	public void addDergonKills(int amount)
	{
		dergonKills += amount;
	}

	public int getClanKills()
	{
		return clanKills;
	}

	public int getClanDeaths()
	{
		return clanDeaths;
	}

	public int getDergonKills()
	{
		return dergonKills;
	}

	private final String id;
	private String leader;
	private String motd;
	private int clanKills = 0;
	private int clanDeaths = 0;
	private int dergonKills = 0;
	private final List<String> members = new ArrayList<String>(0);
}
