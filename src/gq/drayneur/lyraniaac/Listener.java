package gq.drayneur.lyraniaac;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        LyraniaAC.players.add(new PlayerInfo(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        LyraniaAC.players.removeIf(p -> (p.getUuid() == e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onEntityTakeDamage(EntityDamageEvent e) {
        if(e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) e;
            if (entityEvent.getDamager() instanceof Player) {
                Player attacker = (Player)entityEvent.getDamager();
                Entity entity = e.getEntity();
                double distance = entity.getLocation().distance(attacker.getLocation());
                if(distance > 4.0) {
                    e.setCancelled(true);
                    LyraniaAC.getPlayerByUUID(attacker.getUniqueId()).addDetection("reach_basic", 2.0f*(float)distance);
                }
            }
        }
    }

}
