package main.me.volt.dvz.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import main.me.volt.dvz.DvZ;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class GoldRemainingGreaterThan extends Condition {
    int num;

    public boolean initialize(String var) {
        try {
            this.num = Integer.parseInt(var);
            return true;
        }
        catch (NumberFormatException e ){
            return false;
        }
    }

    public boolean check(LivingEntity livingEntity) {
        return (DvZ.plugin.totalVaultAmount > this.num);
    }

    public boolean check(LivingEntity livingEntity, LivingEntity livingEntity1) {
        return (DvZ.plugin.totalVaultAmount > this.num);
    }

    public boolean check(LivingEntity livingEntity, Location location) {
        return (DvZ.plugin.totalVaultAmount > this.num);
    }
}
