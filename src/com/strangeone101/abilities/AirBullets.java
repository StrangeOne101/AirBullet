package com.strangeone101.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;

public class AirBullets extends AirAbility implements AddonAbility {
	
	private boolean charged = false;
	private long chargeTime = 3000L;
	private int bullets = 0;
	private int maxBullets = 3;
	private long cooldown = 5000L;
	
	public AirBullets(Player player) {
		super(player);
		
		if (CoreAbility.hasAbility(player, this.getClass())) {
			AirBullets ability = CoreAbility.getAbility(player, this.getClass());
			ability.bullets++;
			
			new AirBullet(player);
			
			if (ability.bullets >= maxBullets) {
				ability.remove();
				ability.bPlayer.addCooldown(ability);
			}
		} else if (player.isSneaking()) {
			start();
		}
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "AirBullet";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public void progress() {
		if (!bPlayer.canBend(this)) {
			remove();
			return;
		}
		
		if (!charged) {
			if (this.getStartTime() + this.chargeTime < System.currentTimeMillis()) {
				charged = true;
			}
			
			return;
		}
		
		if (charged) {
			//
		}

	}

	@Override
	public String getAuthor() {
		return "StrangeOne101";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public void load() {
		Bukkit.getPluginManager().registerEvents(new AirBulletListener(), ProjectKorra.plugin);
		
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101." + getName() + ".Cooldown", cooldown);
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101." + getName() + ".Range", AirBullet.range);
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101." + getName() + ".ChargeTime", chargeTime);
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101." + getName() + ".Damage", AirBullet.damage);
		ConfigManager.defaultConfig.get().addDefault("ExtraAbilities.StrangeOne101." + getName() + ".MaxBullets", maxBullets);
		
		
		cooldown = ConfigManager.defaultConfig.get().getLong("ExtraAbilities.StrangeOne101." + getName() + ".Cooldown");
		chargeTime = ConfigManager.defaultConfig.get().getLong("ExtraAbilities.StrangeOne101." + getName() + ".ChargeTime");
		maxBullets = ConfigManager.defaultConfig.get().getInt("ExtraAbilities.StrangeOne101." + getName() + ".MaxBullets");
		AirBullet.range = ConfigManager.defaultConfig.get().getDouble("ExtraAbilities.StrangeOne101." + getName() + ".Range");
		AirBullet.damage = ConfigManager.defaultConfig.get().getDouble("ExtraAbilities.StrangeOne101." + getName() + ".Damage");
		
		ConfigManager.defaultConfig.save();
		
		ConfigManager.languageConfig.get().addDefault("Abilities.Air." + getName() + ".DeathMessage", "{victim} was filled with holes by {attacker}'s AirBullets!");
		
		ConfigManager.languageConfig.save();

		Permission perm = new Permission("bending.ability." + getName());
		if (Bukkit.getPluginManager().getPermission("bending.ability." + getName()) == null) {
			Bukkit.getPluginManager().addPermission(perm);
			Bukkit.getPluginManager().getPermission("bending.ability." + getName()).setDefault(PermissionDefault.TRUE);
		}
		
		ProjectKorra.log.info("AirBullet " + getVersion() + " by " + getAuthor() + " enabled!");
		
	}

	@Override
	public void stop() {
		

	}
	
	@Override
	public String getDescription() {
		return "AirBullets allows you to fire air at supersonic speed to pierce your enemies!";
	}
	
	@Override
	public String getInstructions() {
		return "Hold Sneak (SHIFT) to charge the move until you see balls of air appear on your hand. Then, simply click to fire the bullets";
	}

}
