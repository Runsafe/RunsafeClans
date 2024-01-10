package no.runsafe.clans.database;

import no.runsafe.framework.api.database.ISchemaUpdate;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.database.SchemaUpdate;
import no.runsafe.framework.api.player.IPlayer;

import javax.annotation.Nonnull;

public class ClanKillRepository extends Repository
{
	@Nonnull
	@Override
	public String getTableName()
	{
		return "clan_kills";
	}

	@Nonnull
	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();
		update.addQueries(
			"CREATE TABLE `clan_kills` (" +
				"`killer` VARCHAR(36) NOT NULL," +
				"`killerClanID` VARCHAR(3) NULL," +
				"`killed` VARCHAR(36) NOT NULL," +
				"`killedClanID` VARCHAR(3) NULL," +
				"`date` DATETIME NOT NULL" +
			");"
		);
		return update;
	}

	public void recordKill(IPlayer killer, String killerClanID, IPlayer killed, String killedClanID)
	{
		database.execute(
			"INSERT INTO " + getTableName() + " (`killer`, `killerClanID`, `killed`, `killedClanID`, `date`) VALUES(?, ?, ?, ?, NOW())",
			killer, killerClanID, killed, killedClanID
		);
	}

	public void deleteClan(String clanID)
	{
		database.execute("UPDATE " + getTableName() + " SET `killerClanID` = NULL WHERE `killerClanID` = ?", clanID);
		database.execute("UPDATE " + getTableName() + " SET `killedClanID` = NULL WHERE `killedClanID` = ?", clanID);
	}

	public int getPlayerKills(IPlayer player)
	{
		Integer value = database.queryInteger("SELECT COUNT(*) FROM " + getTableName() + " WHERE killer = ?;", player);
		if (value == null)
			return 0;

		return value;
	}

	public int getPlayerDeaths(IPlayer player)
	{
		Integer value = database.queryInteger("SELECT COUNT(*) FROM " + getTableName() + " WHERE killed = ?;", player);
		if (value == null)
			return 0;

		return value;
	}

	public int getClanKills(String clanID, int daysAgo)
	{
		Integer value;
		if (daysAgo < 1)
			value = database.queryInteger("SELECT COUNT(*) FROM " + getTableName() + " WHERE killerClanID = ?;",  clanID);
		else
			value = database.queryInteger(
				"SELECT COUNT(*) FROM " + getTableName() + " WHERE killerClanID = ? AND date >= now() - interval ? day;",
				clanID, daysAgo
			);

		if (value == null)
			return 0;

		return value;
	}

	public int getClanDeaths(String clanID, int daysAgo)
	{
		Integer value;
		if (daysAgo < 1)
			value = database.queryInteger("SELECT COUNT(*) FROM " + getTableName() + " WHERE killedClanID = ?;",  clanID);
		else
			value = database.queryInteger(
				"SELECT COUNT(*) FROM " + getTableName() + " WHERE killedClanID = ? AND date >= now() - interval ? day;",
				clanID, daysAgo
			);

		if (value == null)
			return 0;

		return value;
	}
}
