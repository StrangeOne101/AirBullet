package com.strangeone101.abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.util.DamageHandler;

public class AirBullet extends AirAbility {

	private Location location;
	private Vector direction;
	private Location startLoc;
	public static double moveSpeed = 1D;
	public static double range = 40D;
	public static double damage = 1D;
	
	private List<Entity> damagedEntities = new ArrayList<Entity>();
	
	public AirBullet(Player player) {
		super(player);
		
		this.location = this.startLoc = player.getEyeLocation();
		this.direction = player.getEyeLocation().getDirection();
		
		this.damagedEntities.add(player); //Make the thing ignore the player
		
		start();
	}
	
	@Override
	public long getCooldown() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public String getName() {
		return "AirBullet ";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public void progress() {
		if (this.startLoc.distanceSquared(location) > range * range || GeneralMethods.isSolid(location.getBlock()) || !bPlayer.canBendIgnoreBinds(this)) {
			remove();
			return;
		}
		
		if (this.getCurrentTick() % 5 == 0) AirAbility.playAirbendingSound(location); //Play sound every 1/4 second
		
		for (double d = 0; d < moveSpeed; d += 0.2) {
			location.add(direction.clone().multiply(0.2D));
			AirAbility.playAirbendingParticles(location, 1);
			
			for (Entity e : GeneralMethods.getEntitiesAroundPoint(location, 0.2D)) {
				if (e instanceof LivingEntity && !damagedEntities.contains(e)) {
					DamageHandler.damageEntity(e, damage, this);
					damagedEntities.add(e);
				}
			}
			
			if (GeneralMethods.isSolid(location.getBlock())) {
				remove();
				return;
			}
		}

	}
	
	@Override
	public boolean isHiddenAbility() {
		return true;
	}
	
	@Override
	public double getCollisionRadius() {
		return 0.2D;
	}

}
