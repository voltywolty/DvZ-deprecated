package main.me.volt.dvz.events.doom;

import main.me.volt.dvz.DvZ;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;

import org.bukkit.entity.Player;

public class GolemEvent extends DoomEvent {
    private Spell golemSpell = MagicSpells.getSpellByInternalName("monster_become_golem");

    public GolemEvent(DvZ plugin) {
        super(plugin);
    }

    public String getName() {
        return "GOOOOLLLLEEEEEMMMMMSSSS";
    }

    public void run() {
        for (Player players : this.plugin.monsters) {
            golemSpell.castSpell(players, Spell.SpellCastState.NORMAL, 1.0F, null);
        }
    }
}
