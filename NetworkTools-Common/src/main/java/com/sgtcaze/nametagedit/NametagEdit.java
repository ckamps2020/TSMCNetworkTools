package com.sgtcaze.nametagedit;

import com.sgtcaze.nametagedit.api.INametagApi;
import com.sgtcaze.nametagedit.api.NametagAPI;
import com.sgtcaze.nametagedit.hooks.HookLuckPerms;
import com.sgtcaze.nametagedit.packets.PacketWrapper;
import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;

/**
 * TODO:
 * - Better uniform message format + more messages
 * - Code cleanup
 * - Add language support
 */
public class NametagEdit {

    //TODO clean this up lmfao what ru doing

    //Temp solution until we merge this properly
    private static NametagEdit instance;

    private static INametagApi api;

    private NametagHandler handler;
    private NametagManager manager;

    public static INametagApi getApi() {
        return api;
    }

    public static NametagEdit getInstance() {
        return instance;
    }

    public void onDisable() {
        manager.reset();
        handler.getAbstractConfig().shutdown();
    }

    public void onEnable() {
        instance = this;

        testCompat();
        if (!NetworkTools.getInstance().isEnabled()) return;

        manager = new NametagManager(this);
        handler = new NametagHandler(this, manager);

        PluginManager pluginManager = Bukkit.getPluginManager();
        if (checkShouldRegister("LuckPerms")) {
            pluginManager.registerEvents(new HookLuckPerms(handler), NetworkTools.getInstance());
        }

        NetworkTools.getInstance().getCommand("ne").setExecutor(new NametagCommand(handler));

        if (api == null) {
            api = new NametagAPI(handler, manager);
        }
    }

    void debug(String message) {
        if (handler != null && handler.debug()) {
            NetworkTools.getInstance().getLogger().info("[DEBUG] " + message);
        }
    }

    public NametagHandler getHandler() {
        return handler;
    }

    public NametagManager getManager() {
        return manager;
    }

    private boolean checkShouldRegister(String plugin) {
        if (Bukkit.getPluginManager().getPlugin(plugin) == null) return false;
        NetworkTools.getInstance().getLogger().info("Found " + plugin + "! Hooking in.");
        return true;
    }

    private void testCompat() {
        PacketWrapper wrapper = new PacketWrapper("TEST", "&f", "", 0, new ArrayList<>());
        wrapper.send();
        if (wrapper.error == null) return;
        Bukkit.getPluginManager().disablePlugin(NetworkTools.getInstance());
        NetworkTools.getInstance().getLogger().severe(
                "\n------------------------------------------------------\n" +
                        "[WARNING] NametagEdit v" + NetworkTools.getInstance().getDescription().getVersion() + " Failed to load! [WARNING]" +
                        "\n------------------------------------------------------" +
                        "\nThis might be an issue with reflection. REPORT this:\n> " +
                        wrapper.error +
                        "\nThe plugin will now self destruct.\n------------------------------------------------------");
    }

}