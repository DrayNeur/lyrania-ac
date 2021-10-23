package gq.drayneur.lyraniaac;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LyraniaAC extends JavaPlugin {

    public static HashMap<UUID, HashMap<String, Float>> players = new HashMap<>();

    public boolean isOnEdgeOfBlock(Player p) {
        double X = p.getLocation().getX();
        double Y = p.getLocation().getY() - 1;
        double Z = p.getLocation().getZ();

        if (p.getWorld().getBlockAt(new Location(p.getWorld(), X - 1, Y, Z)).getType() != Material.AIR ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X + 1, Y, Z)).getType() != Material.AIR ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X, Y, Z - 1)).getType() != Material.AIR ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X, Y, Z + 1)).getType() != Material.AIR ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X + 1, Y, Z + 1)).getType() != Material.AIR ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X + 1, Y, Z - 1)).getType() != Material.AIR ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X - 1, Y, Z + 1)).getType() != Material.AIR ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X - 1, Y, Z - 1)).getType() != Material.AIR) {
            return true;
        }
        return false;
    }

    public static void addDetection(UUID uuid, String type, float level) {
        HashMap<String, Float> violationCurrent = LyraniaAC.players.get(uuid);
        if (violationCurrent != null) {
            if (violationCurrent.get(type) == null) {
                violationCurrent.put(type, level);
            } else {
                violationCurrent.put(type, violationCurrent.get(type) + level);
            }
        } else {
            LyraniaAC.players.remove(uuid);
        }
    }
    public static final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
    @Override
    public void onEnable() {
        super.onEnable();
        System.out.println("Lyrania AC started");
        this.getCommand("check").setExecutor(new CheckCommand());

        getServer().getPluginManager().registerEvents(new Listener(), this);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                players.forEach((uuid, violations) -> {
                    violations.forEach((type, level) -> {
                        if (level > 50)
                            Bukkit.getServer().getPlayer(uuid).kickPlayer("§4§lLyraniaAC\n\n§cHack detection: §e" + type + "\n§cLevel: §e" + level + "\n§bSi vous pensez que c'est une érreur contactez nous.");
                    });
                });
            }
        }, 50, 50);
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                players.forEach((uuid, violations) -> {
                    Player p = Bukkit.getServer().getPlayer(uuid);
                    if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                        double lastY = p.getLocation().getY();
                        Location lastLoc = p.getLocation();
                        if(lastY>0) {
                            Bukkit.getScheduler().runTaskLater(LyraniaAC.getPlugin(LyraniaAC.class), () -> {
                                double diffY = p.getLocation().getY() - lastY;
                                double diffYproc = Math.sqrt(Math.pow(diffY, 2.0));
                                if (diffYproc < 0.5) {
                                    int count = 0;
                                    for (count = 0; count > 255 || p.getWorld().getBlockAt(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() - count, p.getLocation().getZ())).getType() == Material.AIR; count++) ;
                                    if (count > 2 && !isOnEdgeOfBlock(p)) {
                                        p.teleport(new Location(p.getWorld(), p.getLocation().getX(),p.getLocation().getY()-(count-1),p.getLocation().getZ()));
                                        addDetection(p.getUniqueId(), "fly_hack_still", 5.0f);
                                    }
                                }
                                if(diffY>2.80) {
                                    p.teleport(lastLoc);
                                    addDetection(p.getUniqueId(), "fly_hack_y", 2.0f*(float)diffY);
                                }

                            }, 20L);
                        }
                    }
                });
            }
        }, 10, 10);
    }
}
