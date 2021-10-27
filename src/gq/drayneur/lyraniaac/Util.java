package gq.drayneur.lyraniaac;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Util {
    public static boolean isInMiddleOfMaterial(Player p, Material mat) {
        double X = p.getLocation().getX();
        double Y = p.getLocation().getY() - 1;
        double Z = p.getLocation().getZ();

        if (p.getWorld().getBlockAt(new Location(p.getWorld(), X - 1, Y, Z)).getType() != mat ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X + 1, Y, Z)).getType() != mat ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X, Y, Z - 1)).getType() != mat ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X, Y, Z + 1)).getType() != mat ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X + 1, Y, Z + 1)).getType() != mat ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X + 1, Y, Z - 1)).getType() != mat ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X - 1, Y, Z + 1)).getType() != mat ||
                p.getWorld().getBlockAt(new Location(p.getWorld(), X - 1, Y, Z - 1)).getType() != mat) {
            return true;
        }
        return false;
    }
}
