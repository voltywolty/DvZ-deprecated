package main.me.volt.dvz.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import main.me.volt.dvz.DvZ;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NearMapSpawnCondition extends Condition {
    int distSq = 10000;

    public boolean initialize(String s) {
        try {
            int dist = Integer.parseInt(s);
            this.distSq = dist * dist;
        }
        catch (NumberFormatException e) {

        }
        return true;
    }

    public boolean check(LivingEntity livingEntity) {
        return (livingEntity.getLocation().distanceSquared(DvZ.plugin.mapSpawn) < this.distSq);
    }

    public boolean check(LivingEntity livingEntity, LivingEntity target) {
        if (target instanceof Player) {
            return check(target);
        }
        return false;
    }

    public boolean check(LivingEntity livingEntity, Location target) {
        return (target.distanceSquared(DvZ.plugin.mapSpawn) < this.distSq);
    }
}
