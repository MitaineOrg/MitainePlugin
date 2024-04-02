package io.papermc.mitaine;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class Aide implements CommandExecutor, TabCompleter {
    private final MitaineMain main;

    public Aide(MitaineMain mitaineMain) {
        this.main = mitaineMain;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {

        if (cmd.getName().equalsIgnoreCase("aide")) {
            FileConfiguration config = main.getConfig();
            String[] commandes = {"courrier","banque","retirer","deposer","donner","icone","leaderboard","mairie","reward","spawn","sethome","home","vote","aide"};
            StringBuilder message = new StringBuilder();
            for (String commande : commandes) {
                message.append(config.getString("important"))
                        .append("/")
                        .append(commande)
                        .append(" ")
                        .append(config.getString("normal"))
                        .append(Objects.requireNonNull(Bukkit.getPluginCommand(commande)).getDescription())
                        .append(config.getString("discret"))
                        .append(" aliases : ")
                        .append(Objects.requireNonNull(Bukkit.getPluginCommand(commande)).getAliases())
                        .append("\n");
            }
            sender.sendMessage(message.toString());
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        return null;
    }
}
