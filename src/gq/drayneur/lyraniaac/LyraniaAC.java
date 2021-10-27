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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LyraniaAC extends JavaPlugin {

    public static ArrayList<PlayerInfo> players = new ArrayList<>();

    public static PlayerInfo getPlayerByUUID(UUID uuid) {
        for (PlayerInfo pInfo:
             players) {
            if(pInfo.getUuid() == uuid)
                return pInfo;
        }
        return null;
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
                for (PlayerInfo pInfo:
                     players) {
                    pInfo.getViolations().forEach((type, level) -> {
                        if (level > 50)
                            Bukkit.getServer().getPlayer(pInfo.getUuid()).kickPlayer("§4§lLyraniaAC\n\n§cHack detection: §e" + type + "\n§cLevel: §e" + level + "\n§bSi vous pensez que c'est une érreur contactez nous.");
                    });
                }
            }
        }, 50, 50);
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (PlayerInfo pInfo:
                     players) {
                    Player p = Bukkit.getServer().getPlayer(pInfo.getUuid());
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
                                    if (count > 2 && !Util.isInMiddleOfMaterial(p, Material.AIR)) {
                                        p.teleport(new Location(p.getWorld(), p.getLocation().getX(),p.getLocation().getY()-(count-1),p.getLocation().getZ()));
                                        pInfo.addDetection("fly_hack_still", 5.0f);
                                    }
                                }
                                if(diffY>2.80) {
                                    p.teleport(lastLoc);
                                    pInfo.addDetection("fly_hack_y", 2.0f*(float)diffY);
                                }
                                if(diffYproc < 0.5 &&
                                        p.getWorld().getBlockAt(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()-1, p.getLocation().getZ())).getType() == Material.STATIONARY_WATER &&
                                        Util.isInMiddleOfMaterial(p, Material.STATIONARY_WATER)) {
                                    pInfo.addDetection("jesus_still", 2.0f);

                                }
                            }, 20L);
                        }
                    }
                }
            }
        }, 10, 10);
    }
}
