package main.me.volt.dvz.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import main.me.volt.dvz.DvZ;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class IsMonsterCondition extends Condition {
    public boolean initialize(String s) {
        return true;
    }

    public boolean check(LivingEntity player) {
        return DvZ.plugin.monsters.contains(player.getName());
    }

    public boolean check(LivingEntity player, LivingEntity target) {
        if (target instanceof Player) {
            return check(target);
        }
        return false;
    }

    public boolean check(LivingEntity player, Location location) {
        return false;
    }
}
