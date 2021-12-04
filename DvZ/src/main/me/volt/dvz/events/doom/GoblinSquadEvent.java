package main.me.volt.dvz.events.doom;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import main.me.volt.dvz.DvZ;
import org.bukkit.entity.Player;

public class GoblinSquadEvent extends DoomEvent {
    private Spell goblinSpell = MagicSpells.getSpellByInternalName("monster_become_goblin");

    public GoblinSquadEvent(DvZ plugin) {
        super(plugin);
    }

    public String getName() {
        return "Goblin Squad";
    }

    public void run() {
        for (Player players : this.plugin.monsters) {
            goblinSpell.castSpell(players, Spell.SpellCastState.NORMAL, 1.0F, null);
            players.playSound(players.getLocation(), "doomevent1", 0.5F, 1.0F);
        }
    }
}
