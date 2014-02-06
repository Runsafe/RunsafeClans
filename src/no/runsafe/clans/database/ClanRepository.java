package no.runsafe.clans.database;

import no.runsafe.clans.Clan;
import no.runsafe.framework.api.database.*;
import no.runsafe.framework.api.player.IPlayer;

import java.util.HashMap;
import java.util.Map;

public class ClanRepository extends Repository
{
	public ClanRepository(IDatabase database)
	{
		this.database = database;
	}

	public Map<String, Clan> getClans()
	{
		Map<String, Clan> clanList = new HashMap<String, Clan>(0);

		for (IRow row : database.query("SELECT `clanID`, `leader` FROM `clans`"))
		{
			String clanName = row.String("clanID");
			clanList.put(clanName, new Clan(clanName, row.String("leader")));
		}
		return clanList;
	}

	public void deleteClan(String clanID)
	{
		database.execute("DELETE FROM `clans` WHERE `clanID` = ?", clanID);
	}

	public void changeClanLeader(String clanID, IPlayer leader)
	{
		database.execute("UPDATE `clans` SET `leader` = ? WHERE `clanID` = ?", leader.getName(), clanID);
	}

	public void persistClan(Clan clan)
	{
		database.execute("INSERT INTO `clans` (`clanID`, `leader`, `started`) VALUES(?, ?, NOW())", clan.getId(), clan.getLeader());
	}

	@Override
	public String getTableName()
	{
		return "clans";
	}

	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
			"CREATE TABLE `clans` (" +
				"`clanID` VARCHAR(3) NOT NULL," +
				"`leader` VARCHAR(20) NOT NULL," +
				"`created` DATETIME NOT NULL," +
				"PRIMARY KEY (`clanID`)" +
			")"
		);

		return update;
	}
}
