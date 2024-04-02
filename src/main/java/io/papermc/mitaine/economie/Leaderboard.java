package io.papermc.mitaine.economie;

import com.google.common.collect.Comparators;
import io.papermc.mitaine.MitaineMain;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.PropertySource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
            if (args.length == 0) {
                ArrayList<Pair<String, Integer>> ld = new ArrayList<>();
                for (OfflinePlayer p : Bukkit.getOnlinePlayers()) {
                    ld.add(new ImmutablePair<>(p.getName(), config.getInt((p.getUniqueId() + ".banque"))));
                }
                for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                    Pair<String, Integer> ofp = new ImmutablePair<>(p.getName(), config.getInt((p.getUniqueId() + ".banque")));
                    if (!ld.contains(ofp)) {
                        ld.add(ofp);
                    }
                }
                ld.sort((a,b) -> (b.getValue().compareTo((a.getValue()))));
                StringBuilder message = new StringBuilder();
                int i = 1;
                for (Pair<String, Integer> j : ld){
                    message.append(config.getString("discret"))
                            .append(i++)
                            .append(" - ")
                            .append(config.getString("important"))
                            .append(j.getKey())
                            .append(config.getString("normal"))
                            .append(" : ")
                            .append(config.getString("important"))
                            .append(j.getValue())
                            .append(config.getString("normal"))
                            .append(" diamants\n");
                }
                sender.sendMessage(message.toString());
            } else {
                sender.sendMessage(config.getString("erreur") + "La commande est /" + cmd.getName());
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        return null;
    }
}
