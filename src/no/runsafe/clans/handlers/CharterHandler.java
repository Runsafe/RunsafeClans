package no.runsafe.clans.handlers;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeBook;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

import java.util.Collections;
import java.util.List;

public class CharterHandler
{
	public void givePlayerCharter(IPlayer player, String clanName)
	{
		RunsafeMeta charter = Item.Miscellaneous.BookAndQuill.getItem(); // Create a book item.
		charter.setDisplayName("Leather-bound Charter"); // Give the item a name.
		charter.addLore("§7Clan: " + clanName); // Append the clan name.
		charter.addLore("§7Leader: " + player.getName()); // Append the clan leader.
		charter.addLore("§fRight-click to sign the clan charter!"); // Add some info.

		player.give(charter); // Give the player the charter.
	}

	public boolean itemIsCharter(RunsafeMeta item)
	{
		List<String> lore = item.getLore();
		return lore != null && lore.size() == 3 && lore.get(2).equals("§fRight-click to sign the clan charter!");
	}

	public String getClanName(RunsafeMeta charter)
	{
		return getCharterValue(charter.getLore(), 1);
	}

	public String getLeaderName(RunsafeMeta charter)
	{
		return getCharterValue(charter.getLore(), 2);
	}

	private String getCharterValue(List<String> values, int index)
	{
		if (values == null)
			return "INVALID"; // This should never happen.

		return values.get(index).split("\\s")[1];
	}

	public List<String> getCharterSigns(RunsafeMeta item)
	{
		RunsafeBook charter = (RunsafeBook) item; // Convert item to a book.
		if (charter.hasPages()) // Check we have some pages.
			return charter.getPages(); // Return the pages.

		return Collections.emptyList(); // Return an empty thing.
	}

	public void addCharterSign(RunsafeMeta item, String playerName)
	{
		RunsafeBook charter = (RunsafeBook) item; // Convert item to a book.
		charter.addPages(playerName); // Add the sign to the charter.
	}
}
