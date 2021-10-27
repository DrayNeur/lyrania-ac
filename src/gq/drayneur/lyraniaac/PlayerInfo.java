package gq.drayneur.lyraniaac;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInfo {
    private UUID uuid;
    private HashMap<String, Float> violations;
    private boolean isStill = false;
    private boolean isGrounded = false;
    private boolean isTeleporting = false;
    private long currentTick;

    public PlayerInfo(UUID uuid) {
        this.uuid = uuid;
        this.violations = new HashMap<>();
        this.isStill = false;
        this.isGrounded = false;
    }
    public void update() {
        Player p = Bukkit.getPlayer(this.uuid);
        this.currentTick++;
    }
    public void addDetection(String type, float level) {
        if (violations.get(type) == null) {
            violations.put(type, level);
        } else {
            violations.put(type, violations.get(type) + level);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public HashMap<String, Float> getViolations() {
        return violations;
    }
}
