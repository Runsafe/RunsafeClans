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

	private final String id;
	private String leader;
	private String motd;
	private final List<String> members = new ArrayList<String>(0);
}
