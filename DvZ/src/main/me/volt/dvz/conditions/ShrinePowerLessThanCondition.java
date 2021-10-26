package main.me.volt.dvz.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import main.me.volt.dvz.DvZ;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class ShrinePowerLessThanCondition extends Condition {
    int power;

    public boolean initialize(String s) {
        try {
            this.power = Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean check(LivingEntity livingEntity) {
        return (DvZ.plugin.shrinePower < this.power);
    }

    public boolean check(LivingEntity livingEntity, LivingEntity livingEntity1) {
        return check(livingEntity);
    }

    public boolean check(LivingEntity livingEntity, Location location) {
        return check(livingEntity);
    }
}
