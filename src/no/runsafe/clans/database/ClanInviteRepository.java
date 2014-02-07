package no.runsafe.clans.database;

import no.runsafe.framework.api.database.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanInviteRepository extends Repository
{
	public ClanInviteRepository(IDatabase database)
	{
		this.database = database;
	}

	public Map<String, List<String>> getPendingInvites()
	{
		Map<String, List<String>> map = new HashMap<String, List<String>>(0);

		for (IRow row : database.query("SELECT `clanID`, `player` FROM `clan_invites`"))
		{
			String playerName = row.String("player");
			if (!map.containsKey(playerName))
				map.put(playerName, new ArrayList<String>(0));

			map.get(playerName).add(row.String("clanID"));
		}

		return map;
	}

	public void clearAllPendingInvites(String playerName)
	{
		database.execute("DELETE FROM `clan_invites` WHERE `player` = ?", playerName);
	}

	public void clearAllPendingInvitesForClan(String clanID)
	{
		database.execute("DELETE FROM `clan_invites` WHERE `clanID` = ?", clanID);
	}

	@Nonnull
	@Override
	public String getTableName()
	{
		return "clan_invites";
	}

	@Nonnull
	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
			"CREATE TABLE `clan_invites` (" +
				"`clanID` VARCHAR(3) NOT NULL," +
				"`player` VARCHAR(20) NOT NULL," +
				"PRIMARY KEY (`clanID`, `player`)" +
			")"
		);

		return update;
	}
}
