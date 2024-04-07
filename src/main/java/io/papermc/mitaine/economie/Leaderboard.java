package io.papermc.mitaine.economie;

import io.papermc.mitaine.MitaineMain;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
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
                if (args[0].equalsIgnoreCase("diamants") || args[0].equalsIgnoreCase("diamant") || args[0].equalsIgnoreCase("diams")) {
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
                    ld.sort((a, b) -> (b.getValue().compareTo((a.getValue()))));
                    StringBuilder message = new StringBuilder();
                    int i = 1;
                    for (Pair<String, Integer> j : ld) {
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
                } else if (args[0].equalsIgnoreCase("temps") || args[0].equalsIgnoreCase("time")) {
                    ArrayList<Pair<String, Double>> ld = new ArrayList<>();
                    for (OfflinePlayer p : Bukkit.getOnlinePlayers()) {
                        ld.add(new ImmutablePair<>(p.getName(), p.getStatistic(Statistic.TOTAL_WORLD_TIME)/72000.0));
                    }
                    for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                        Pair<String, Double> ofp = new ImmutablePair<>(p.getName(), p.getStatistic(Statistic.TOTAL_WORLD_TIME)/72000.0);
                        if (!ld.contains(ofp)) {
                            ld.add(ofp);
                        }
                    }
                    ld.sort((a, b) -> (b.getValue().compareTo((a.getValue()))));
                    StringBuilder message = new StringBuilder();
                    int i = 1;
                    DecimalFormat df = new DecimalFormat("#.#");
                    for (Pair<String, Double> j : ld) {
                        message.append(config.getString("discret"))
                                .append(i++)
                                .append(" - ")
                                .append(config.getString("important"))
                                .append(j.getKey())
                                .append(config.getString("normal"))
                                .append(" : ")
                                .append(config.getString("important"))
                                .append(df.format(j.getValue()))
                                .append(config.getString("normal"))
                                .append(" heures\n");
                    }
                    sender.sendMessage(message.toString());

                } else {
                    sender.sendMessage(config.getString("erreur") + "La commande est /" + cmd.getName() + " <diamants | temps>");
                }
            } else {
                sender.sendMessage(config.getString("erreur") + "La commande est /" + cmd.getName() + " <diamants | temps>");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        return null;
    }
}
