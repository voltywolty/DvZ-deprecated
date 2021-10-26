package main.me.volt.dvz.passives;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShrineDestroyedListener extends PassiveListener {
    private static List<PassiveSpell> spells = new ArrayList<>();

    public void registerSpell(PassiveSpell spell, String var) {
        spells.add(spell);
    }

    public void initialize(String s) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Spellbook spellbook = MagicSpells.getSpellbook(player);
            for (PassiveSpell spell : spells) {
                if (spellbook.hasSpell(spell)) {
                    spell.activate(player);
                }
            }
        }
    }
}
