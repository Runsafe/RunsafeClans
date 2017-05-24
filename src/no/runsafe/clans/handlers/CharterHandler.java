package no.runsafe.clans.handlers;

import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeBook;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CharterHandler
{
	/**
	 * Constructor for handling clan charters.
	 * @param server The server. Used for getting a player object from their username and or UUID.
	 */
	public CharterHandler(IServer server)
	{
		this.server = server;
	}

	public void givePlayerCharter(IPlayer player, String clanName)
	{
		RunsafeMeta charter = Item.Special.Crafted.WrittenBook.getItem(); // Create a book item.
		charter.setDisplayName("Leather-bound Charter"); // Give the item a name.
		charter.addLore("§7Clan: " + clanName); // Append the clan name.
		charter.addLore("§7Leader: " + player.getName()); // Append the clan leader.
		charter.addLore("§fRight-click to sign the clan charter!"); // Add some info.
		addCharterSign(charter, player); // Sign the charter.

		player.give(charter); // Give the player the charter.
	}

	public boolean itemIsCharter(RunsafeMeta item)
	{
		List<String> lore = item.getLore();
		return lore != null && lore.size() == 3 && lore.get(2).equals("§fRight-click to sign the clan charter!");
	}

	public String getClanName(RunsafeMeta charter)
	{
		return getCharterValue(charter.getLore(), 0);
	}

	public IPlayer getLeader(RunsafeMeta charter)
	{
		return getCharterSigns(charter).get(0);
	}

	private String getCharterValue(List<String> values, int index)
	{
		if (values == null)
			return "INVALID"; // This should never happen.

		return values.get(index).split("\\s")[1];
	}

	public List<IPlayer> getCharterSigns(RunsafeMeta item)
	{
		RunsafeBook charter = (RunsafeBook) item; // Convert item to a book.
		if (charter.hasPages()) // Check we have some pages.
		{
			List<String> charterPages = charter.getPages();
			List<IPlayer> charterSigns = new ArrayList<IPlayer>(0);
			for(String page : charterPages)
				charterSigns.add(server.getPlayer(UUID.fromString(page)));
			return charterSigns;
		}

		return Collections.emptyList(); // Return an empty thing.
	}

	public void addCharterSign(RunsafeMeta item, IPlayer player)
	{
		RunsafeBook charter = (RunsafeBook) item; // Convert item to a book.
		charter.addPages(player.getUniqueId().toString()); // Add the sign to the charter.
	}

	private final IServer server;
}
