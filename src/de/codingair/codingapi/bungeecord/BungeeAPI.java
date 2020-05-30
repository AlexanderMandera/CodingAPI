package de.codingair.codingapi.bungeecord;

import de.codingair.codingapi.bungeecord.listeners.ChatButtonListener;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.utils.Ticker;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BungeeAPI {
    private static BungeeAPI instance;
    private Plugin plugin;

    private static final List<Ticker> TICKERS = new ArrayList<>();
    private Timer tickerTimer = null;

    public void onEnable(Plugin plugin) {
        this.plugin = plugin;
        BungeeCord.getInstance().getPluginManager().registerListener(plugin, (Listener) IReflection.getConstructor(ChatButtonListener.class).newInstance());
    }

    public void onDisable() {
        if(tickerTimer != null) {
            tickerTimer.cancel();
            tickerTimer = null;
        }

        BungeeCord.getInstance().getPluginManager().unregisterListeners(plugin);
    }

    private void runTicker() {
        if(this.tickerTimer != null) return;

        this.tickerTimer = new Timer();

        this.tickerTimer.schedule(new TimerTask() {
            int second = 0;

            @Override
            public void run() {
                List<Ticker> tickers = new ArrayList<>(TICKERS);
                for(Ticker ticker : tickers) {
                    ticker.onTick();
                }

                if(second == 20) {
                    second = 0;

                    for(Ticker ticker : tickers) {
                        ticker.onSecond();
                    }
                } else second++;

                tickers.clear();
            }
        }, 50, 50);
    }

    public static void addTicker(Ticker ticker) {
        TICKERS.add(ticker);
        getInstance().runTicker();
    }

    public static boolean removeTicker(Ticker ticker) {
        return TICKERS.remove(ticker);
    }

    public static BungeeAPI getInstance() {
        if(instance == null) instance = new BungeeAPI();
        return instance;
    }

    public static boolean isEnabled() {
        return instance != null;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
