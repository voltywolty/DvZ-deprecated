package main.me.volt.dvz.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import main.me.volt.dvz.DvZ;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class MonstersReleasedCondition extends Condition {

    public boolean initialize(String s) {
        return true;
    }

    public boolean check(LivingEntity livingEntity) {
        return DvZ.plugin.monstersReleasedFully;
    }

    public boolean check(LivingEntity player, LivingEntity target) {
        return DvZ.plugin.monstersReleasedFully;
    }

    public boolean check(LivingEntity player, Location target) {
        return DvZ.plugin.monstersReleasedFully;
    }
}
