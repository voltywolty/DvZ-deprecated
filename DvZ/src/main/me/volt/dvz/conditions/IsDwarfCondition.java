package main.me.volt.dvz.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import main.me.volt.dvz.DvZ;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class IsDwarfCondition extends Condition {
    public boolean initialize(String s) {
        return true;
    }

    public boolean check(LivingEntity livingEntity) {
        System.out.println("ISDWARF (player): " + livingEntity.getName() + " " + (DvZ.plugin.dwarves.contains(livingEntity.getName()) ? "yes" : "no"));
        return DvZ.plugin.dwarves.contains(livingEntity.getName());
    }

    public boolean check(LivingEntity livingEntity, LivingEntity target) {
        System.out.println("ISDWARF (target): " + livingEntity.getName() + " + " + target);

        if (target instanceof Player) {
            System.out.println("IS PLAYER");
            return check(target);
        }
        return false;
    }

    public boolean check(LivingEntity livingEntity, Location location) {
        return false;
    }
}
