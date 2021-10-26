package main.me.volt.dvz.events.doom;

import main.me.volt.dvz.DvZ;

import java.util.Random;

public abstract class DoomEvent {
    protected DvZ plugin;

    protected Random random;

    public DoomEvent(DvZ plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public abstract String getName();

    public abstract void run();
}
