package io.papermc.mitaine.economie;

import io.papermc.mitaine.MitaineMain;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard implements CommandExecutor, TabCompleter {
    private final MitaineMain main;
    public Leaderboard(MitaineMain mitaineMain) {
        this.main = mitaineMain;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        if (cmd.getName().equalsIgnoreCase("leaderboard")) {
            FileConfiguration config = main.getConfig();
            if (args.length == 1) {
                ArrayList<String> commandes = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {

                }
            } else {
                sender.sendMessage(config.getString("erreur") + "La commande est " + cmd + " <joueur|equipe>");
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        return null;
    }
}
