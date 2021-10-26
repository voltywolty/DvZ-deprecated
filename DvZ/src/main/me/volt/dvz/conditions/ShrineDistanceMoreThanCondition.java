package main.me.volt.dvz.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import main.me.volt.dvz.DvZ;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ShrineDistanceMoreThanCondition extends Condition {
    double distSq;

    public boolean initialize(String s) {
        try {
            this.distSq *= Double.parseDouble(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean check(LivingEntity livingEntity) {
        return (DvZ.plugin.shrineManager.getCurrentShrine().distanceSquared(livingEntity.getLocation()) >= this.distSq);
    }

    public boolean check(LivingEntity livingEntity, LivingEntity livingEntity1) {
        if (livingEntity instanceof Player) {
            return check(livingEntity1);
        }
        return false;
    }

    public boolean check(LivingEntity livingEntity, Location location) {
        return (DvZ.plugin.shrineManager.getCurrentShrine().distanceSquared(location) >= this.distSq);
    }
}
