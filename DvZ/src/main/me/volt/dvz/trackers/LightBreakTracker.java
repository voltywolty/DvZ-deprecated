package main.me.volt.dvz.trackers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LightBreakTracker {
    private final int countForWarn = 2;
    private final int countForKill = 6;
    private final int time = 20000;

    private Map<String, Tracker> map = new HashMap<>();

    public void addLightBreak(Player player) {
        Tracker tracker = this.map.get(player.getName());

        if (tracker == null) {
            tracker = new Tracker();
            this.map.put(player.getName(), tracker);
        }

        tracker.add();
        CheckResult check = tracker.check();

        if (check == CheckResult.WARN) {
            player.sendMessage(ChatColor.RED + "Stop breaking the lights dwarf!");
        }
        else if (check == CheckResult.KILL) {
            player.kickPlayer(ChatColor.RED + "You have been kicked for destroying lights as a dwarf.");
        }
    }

    private class Tracker {
        long[] times = new long[6];

        int pointer = 0;

        void add() {
            this.times[this.pointer] = System.currentTimeMillis();
            this.pointer++;

            if (this.pointer >= 6) {
                this.pointer = 0;
            }
        }

        LightBreakTracker.CheckResult check() {
            long now = System.currentTimeMillis();
            int c = 0;

            for (int i = 0; i < this.times.length; i++) {
                if (this.times[i] > 0L && this.times[i] > now - 20000L) {
                    c++;
                }
            }

            if ( c >= 6) {
                return LightBreakTracker.CheckResult.KILL;
            }

            if (c >= 2) {
                return LightBreakTracker.CheckResult.WARN;
            }

            return LightBreakTracker.CheckResult.NOTHING;
        }

        private Tracker() {

        }
    }

    private enum CheckResult {
        NOTHING, WARN, KILL
    }
}
