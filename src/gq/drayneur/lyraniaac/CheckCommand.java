package gq.drayneur.lyraniaac;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CheckCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (strings.length==0 || strings.length>1) {
                p.sendMessage("§cUsage /check <player>");
                return true;
            }
            Player checkingPlayer = Bukkit.getPlayer(strings[0]);
            if(checkingPlayer == null) {
                p.sendMessage("§cThe player does not exist !");
                return true;
            }
            HashMap<String, Float> violation = LyraniaAC.getPlayerByUUID(checkingPlayer.getUniqueId()).getViolations();
            p.sendMessage("§e Violations of "+strings[0]+"\n§e---");
            if(violation.size() == 0) {
                p.sendMessage("§cThis player has no violation hack !");
            }
            violation.forEach((type, level) -> {
                String color = (level < 12.5 ? "§a" : level < 25 ? "§2" : level < 37.5 ? "§c" : "§4");
                p.sendMessage("§3"+type+":"+color+level);
            });
            p.sendMessage("§e---");
        }
        return true;
    }
}
