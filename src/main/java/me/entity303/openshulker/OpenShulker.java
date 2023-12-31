package me.entity303.openshulker;

import me.entity303.openshulker.commands.OpenShulkerCommand;
import me.entity303.openshulker.listener.ShulkerDupeListener;
import me.entity303.openshulker.listener.ShulkerOpenCloseListener;
import me.entity303.openshulker.listener.ShulkerReadOnlyListener;
import me.entity303.openshulker.util.ShulkerActions;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class OpenShulker extends JavaPlugin implements Listener {
    private ShulkerActions shulkerActions;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.reloadConfig();

        String openSound = this.getConfig().getString("OpenSound");

        try {
            Sound.valueOf(openSound);
        } catch (Throwable ignored) {
            if (openSound == null) {
                Bukkit.getLogger().severe("You did not specify OpenSound, using default");
                this.getConfig().set("OpenSound", "BLOCK_SHULKER_BOX_OPEN");
                this.saveConfig();
                this.reloadConfig();
            } else
                Bukkit.getLogger().severe("There is no sound called '" + openSound + "', for a list of sounds, visit https://www.spigotmc.org/wiki/cc-sounds-list/");
        }

        String closeSound = this.getConfig().getString("CloseSound");

        try {
            Sound.valueOf(closeSound);
        } catch (Throwable ignored) {
            if (closeSound == null) {
                Bukkit.getLogger().severe("You did not specify CloseSound, using default");
                this.getConfig().set("OpenSound", "BLOCK_SHULKER_BOX_CLOSE");
                this.saveConfig();
                this.reloadConfig();
            } else
                Bukkit.getLogger().severe("There is no sound called '" + closeSound + "', for a list of sounds, visit https://www.spigotmc.org/wiki/cc-sounds-list/");
        }

        this.shulkerActions = new ShulkerActions(this);

        Bukkit.getPluginManager().registerEvents(new ShulkerOpenCloseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ShulkerDupeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ShulkerReadOnlyListener(this), this);

        OpenShulkerCommand openShulkerCommand = new OpenShulkerCommand(this);

        PluginCommand command = this.getCommand("openshulker");

        command.setExecutor(openShulkerCommand);
        command.setTabCompleter(openShulkerCommand);
    }

    @Override
    public void onDisable() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!this.shulkerActions.hasOpenShulkerBox(all))
                continue;

            ItemStack shulkerBox = this.shulkerActions.searchShulkerBox(all);

            if (shulkerBox == null)
                continue;

            this.shulkerActions.saveShulkerBox(shulkerBox, all.getOpenInventory().getTopInventory(), all);

            all.closeInventory();
        }
    }

    public ShulkerActions getShulkerActions() {
        return this.shulkerActions;
    }
}
