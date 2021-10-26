package main.me.volt.dvz.events.doom;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import main.me.volt.dvz.DvZ;
import org.bukkit.entity.Player;

public class DireWolvesEvent extends DoomEvent {
    private Spell wolfSpell = MagicSpells.getSpellByInternalName("monster_become_wolf");

    public DireWolvesEvent(DvZ plugin) {
        super(plugin);
    }

    public String getName() {
        return "Dire Wolves";
    }

    public void run() {
        for (Player players : this.plugin.monsters) {
            wolfSpell.castSpell(players, Spell.SpellCastState.NORMAL, 1.0F, null);
        }
    }
}
