package no.runsafe.clans.database;

import no.runsafe.framework.api.database.*;
import no.runsafe.framework.api.player.IPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanMemberRepository extends Repository
{
	public ClanMemberRepository(IDatabase database)
	{
		this.database = database;
	}

	public Map<String, List<String>> getClanRosters()
	{
		Map<String, List<String>> rosters = new HashMap<String, List<String>>(0);
		for (IRow row : database.query("SELECT `clanID`, `member` FROM `clan_members`"))
		{
			String clanName = row.String("clanID");
			if (!rosters.containsKey(clanName))
				rosters.put(clanName, new ArrayList<String>(1));

			rosters.get(clanName).add(row.String("member"));
		}
		return rosters;
	}

	public void addClanMember(String clanID, String playerName)
	{
		database.execute("INSERT INTO `clan_members` (`clanID`, `member`) VALUES(?, ?)", clanID, playerName);
	}

	public void removeClanMember(String playerName)
	{
		removeClanMemberByName(playerName);
	}

	public void removeClanMemberByName(String playerName)
	{
		database.execute("DELETE FROM `clan_members` WHERE `member` = ?", playerName);
	}

	public void removeAllClanMembers(String clanID)
	{
		database.execute("DELETE FROM `clan_members` WHERE `clanID` = ?", clanID);
	}

	@Override
	public String getTableName()
	{
		return "clan_members";
	}

	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
			"CREATE TABLE `clan_members` (" +
				"`clanID` VARCHAR(3) NOT NULL," +
				"`member` VARCHAR(20) NOT NULL," +
				"PRIMARY KEY (`clanID`, `member`)" +
			")"
		);

		return update;
	}
}
