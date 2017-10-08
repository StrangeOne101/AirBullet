package com.strangeone101.abilities;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class AirBulletListener implements Listener {
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {
		if (event.isCancelled() || !event.isSneaking()) return;
		
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
		
		if (bPlayer != null && bPlayer.canBend(CoreAbility.getAbility(AirBullets.class)) && !CoreAbility.hasAbility(event.getPlayer(), AirBullets.class)) {
			new AirBullets(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onSwing(PlayerAnimationEvent event) {
		if (event.isCancelled()) return;
		
		if (CoreAbility.hasAbility(event.getPlayer(), AirBullets.class)) {
			new AirBullets(event.getPlayer()); //Fire bullet
		}
	}

}
