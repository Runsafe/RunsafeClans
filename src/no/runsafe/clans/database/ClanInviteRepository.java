package no.runsafe.clans.database;

import no.runsafe.framework.api.database.*;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.api.server.IPlayerProvider;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClanInviteRepository extends Repository
{
	public ClanInviteRepository(IDatabase database, IPlayerProvider playerProvider)
	{
		this.database = database;
		this.playerProvider = playerProvider;
	}

	public Map<IPlayer, List<String>> getPendingInvites()
	{
		Map<IPlayer, List<String>> map = new HashMap<>(0);

		for (IRow row : database.query("SELECT `clanID`, `player` FROM `clan_invites`"))
		{
			IPlayer player = playerProvider.getPlayer(UUID.fromString(row.String("player")));
			if (!map.containsKey(player))
				map.put(player, new ArrayList<>(0));

			map.get(player).add(row.String("clanID"));
		}

		return map;
	}

	public void clearPendingInvite(IPlayer player, String clanID)
	{
		database.execute("DELETE FROM `clan_invites` WHERE `player` = ? AND `clanID` = ?", player.getUniqueId().toString(), clanID);
	}

	public void clearAllPendingInvites(IPlayer player)
	{
		database.execute("DELETE FROM `clan_invites` WHERE `player` = ?", player.getUniqueId().toString());
	}

	public void clearAllPendingInvitesForClan(String clanID)
	{
		database.execute("DELETE FROM `clan_invites` WHERE `clanID` = ?", clanID);
	}

	public void addInvite(IPlayer player, String clanID)
	{
		database.execute("INSERT IGNORE INTO `clan_invites` (`player`, `clanID`) VALUES(?, ?)", player.getUniqueId().toString(), clanID);
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

		update.addQueries(
			String.format("ALTER TABLE `%s` MODIFY COLUMN player VARCHAR(36)", getTableName()),
			String.format( // Player names -> Unique IDs
				"UPDATE IGNORE `%s` SET `player` = " +
					"COALESCE((SELECT `uuid` FROM player_db WHERE `name`=`%s`.`player`), `player`) " +
					"WHERE length(`player`) != 36",
				getTableName(), getTableName()
			)
		);

		return update;
	}

	private final IPlayerProvider playerProvider;
}
