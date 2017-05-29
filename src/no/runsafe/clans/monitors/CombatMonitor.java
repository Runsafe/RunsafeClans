package no.runsafe.clans.monitors;

import no.runsafe.clans.Clan;
import no.runsafe.clans.Config;
import no.runsafe.clans.events.BackstabberEvent;
import no.runsafe.clans.events.MutinyEvent;
import no.runsafe.clans.handlers.ClanHandler;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.IUniverse;
import no.runsafe.framework.api.entity.IProjectileSource;
import no.runsafe.framework.api.event.entity.IEntityDamageByEntityEvent;
import no.runsafe.framework.api.event.player.IPlayerDeathEvent;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.entity.ProjectileEntity;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.entity.RunsafeLivingEntity;
import no.runsafe.framework.minecraft.entity.RunsafeProjectile;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageByEntityEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerDeathEvent;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CombatMonitor implements IEntityDamageByEntityEvent, IPlayerDeathEvent
{
	public CombatMonitor(IServer server, IScheduler scheduler, ClanHandler clanHandler, Config config)
	{
		this.server = server;
		this.scheduler = scheduler;
		this.clanHandler = clanHandler;
		this.config = config;
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event)
	{
		IPlayer deadPlayer = event.getEntity();
		String deadPlayerName = deadPlayer.getName();

		// Check we tracked the player getting hit and they are in a clan!
		if (!track.containsKey(deadPlayer) || !clanHandler.playerIsInClan(deadPlayerName))
			return;

		String killerName = track.get(deadPlayer).getAttacker().getName(); // Grab the name of the last player to hit them.
		if (!clanHandler.playerIsInClan(killerName))
			return;

		Clan deadPlayerClan = clanHandler.getPlayerClan(deadPlayerName); // Dead players clan.
		if (clanHandler.playerIsInClan(killerName, deadPlayerClan.getId()))
		{
			IPlayer thePlayer = server.getPlayerExact(killerName);

			if (thePlayer != null)
			{
				new BackstabberEvent(thePlayer).Fire();
				if (clanHandler.playerIsClanLeader(deadPlayerName))
					new MutinyEvent(thePlayer).Fire();
			}
		}
		else
		{
			clanHandler.addClanKill(killerName); // Stat the kill
			clanHandler.addClanDeath(deadPlayerName); // Stat the death
		}
	}

	@Override
	public void OnEntityDamageByEntity(RunsafeEntityDamageByEntityEvent event)
	{
		if (!(event.getEntity() instanceof IPlayer))
			return;

		IPlayer victim = (IPlayer) event.getEntity();
		if (victim.isVanished())
			return;

		IUniverse universe = victim.getUniverse();
		if (universe == null || !config.getClanUniverse().contains(universe.getName()))
			return;

		IPlayer source = null;
		RunsafeEntity attacker = event.getDamageActor();

		if (attacker instanceof IPlayer)
			source = (IPlayer) attacker;
		else if (attacker instanceof RunsafeProjectile)
		{
			RunsafeProjectile projectile = (RunsafeProjectile) attacker;
			if (!(projectile.getEntityType() == ProjectileEntity.Egg || projectile.getEntityType() == ProjectileEntity.Snowball))
				source = projectile.getShootingPlayer();
		}

		if (source == null || source.isVanished() || source.shouldNotSee(victim) || victim.equals(source))
			return;

		registerHit(victim, source); // Register the hit!
	}

	private void registerHit(IPlayer victim, IPlayer attacker)
	{
		// Check to see if we have a timer existing.
		if (track.containsKey(victim))
			scheduler.cancelTask(track.get(victim).getTimerID()); // Cancel existing timer.
		else
			track.put(victim, new CombatTrackingNode()); // Create blank node.

		// Update the node with new information.
		track.get(victim).setAttacker(attacker).setTimerID(scheduler.startAsyncTask(new Runnable()
		{
			@Override
			public void run()
			{
				track.remove(victim); // Remove after 10 seconds.
			}
		}, 10));
	}

	private IPlayer findPlayer(RunsafeLivingEntity entity)
	{
		List<IPlayer> onlinePlayers = server.getOnlinePlayers();
		for (IPlayer player : onlinePlayers)
			if (entity != null && player != null && entity.getEntityId() == player.getEntityId())
				return player;

		return null;
	}

	private final IServer server;
	private final IScheduler scheduler;
	private final ClanHandler clanHandler;
	private final Config config;
	private final ConcurrentHashMap<IPlayer, CombatTrackingNode> track = new ConcurrentHashMap<>(0);
}
