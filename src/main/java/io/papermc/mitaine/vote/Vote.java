package io.papermc.mitaine.vote;

import io.papermc.mitaine.MitaineMain;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class Vote implements CommandExecutor, Listener {

    private static int nb_choix = 0;
    private static final ArrayList<Integer> votes = new ArrayList<>();
    private static final ArrayList<UUID> votants = new ArrayList<>();
    private static final NamespacedKey cleBar = new NamespacedKey("vote", "vote");
    private static BossBar barVote = null;
    private static final StringBuilder bc = new StringBuilder();
    private final MitaineMain main;

    public Vote(MitaineMain main) {
        this.main = main;
        barVote = Bukkit.createBossBar(cleBar, "Un vote est en cours faites " + main.getConfig().getString("important") + "/vote", BarColor.RED, BarStyle.SEGMENTED_20);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent join) {
        Player player = join.getPlayer();
        if (nb_choix != 0) {
            FileConfiguration config = main.getConfig();
            player.sendMessage(config.getString("titre") + " Un vote est en cours, faites " + config.getString("important") + "/vote" + config.getString("normal") + " pour voter.\n" + bc);
            barVote.addPlayer(player);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {

        FileConfiguration config = main.getConfig();
        if (cmd.getName().equalsIgnoreCase("creervote")) {
            if (args.length > 1) {
                votes.add(0);
                for (String choix : args) {
                    nb_choix++;
                    bc.append("- ").append(config.getString("important")).append(nb_choix).append(config.getString("normal")).append(" pour ").append(choix).append("\n"); // Ajouter voter cliquable
                    votes.add(0);
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(config.getString("titre") + " Un vote est en cours, faites " + config.getString("important") + "/vote" + config.getString("normal") + " pour voter.\n" + bc);
                    barVote.addPlayer(p);
                }
                barVote.setProgress(1.0);
            } else {
                sender.sendMessage(config.getString("erreur") + " La commande est : /creervote <choix 1> ... <choix n>");
            }
            return true;

        } else if (cmd.getName().equalsIgnoreCase("vote")) {
            if (sender instanceof Player player) {
                UUID pId = player.getUniqueId();
                if (args.length == 1 && !votants.contains(pId)) {
                    if (nb_choix == 0) {
                        player.sendMessage(config.getString("titre") + " Il n'y a pas de vote en cours");
                    } else {
                        try {
                            int choix = Integer.parseInt(args[0]);
                            if (choix <= 0 || choix > nb_choix) {
                                player.sendMessage(config.getString("erreur") + " Vous devez rentrer un chiffre compris entre 1 et " + nb_choix);
                            } else {
                                votes.set(choix, votes.get(choix) + 1);
                                votants.add(pId);
                                player.sendMessage(config.getString("titre") + " Merci, vous avez voté : " + config.getString("important") + choix);
                            }
                        } catch (NumberFormatException e) {
                            player.sendMessage(config.getString("erreur") + " Vous devez entrer un chiffre en paramètre");
                        }
                    }
                } else {
                    if (args.length != 1) {
                        player.sendMessage(config.getString("erreur") + " La commande est : /vote <choix>");
                    } else if (votants.contains(pId)) {
                        player.sendMessage(config.getString("titre") + " Vous ne pouvez pas voter plusieurs fois avec le même compte");
                    }
                }
                return true;
            }

        } else if (cmd.getName().equalsIgnoreCase("resultats")) {
            if (nb_choix == 0) {
                sender.sendMessage(config.getString("titre") + " Il n'y a pas de vote pour le moment");
            } else {
                StringBuilder res = new StringBuilder();
                for (int i = 1; i <= nb_choix; i++) {
                    res.append("- ").append(config.getString("important")).append(votes.get(i)).append(config.getString("normal")).append(" pour le vote ").append(i).append("\n");
                }
                sender.sendMessage(config.getString("titre") + " Le résultat du vote est :\n" + res);
                int idMax = 0;
                int sum = 0;
                for (int v = 0; v < votes.size(); v++) {
                    sum += votes.get(v);
                    if (votes.get(v) > votes.get(idMax)) {
                        idMax = v;
                    }
                }
                if (sum != 0) {
                    String message = config.getString("titre") + " Le choix " + config.getString("important") + "n°" + idMax + config.getString("normal") + " remporte le vote avec " + config.getString("important") + (double) 100 * votes.get(idMax) / sum + "%" + config.getString("normal") + " des voix";
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(message);
                    }
                } else {
                    sender.sendMessage(config.getString("erreur") + "  Il n'y a eu aucun vote");
                }
                nb_choix = 0;
                votes.clear();
                votants.clear();
                barVote.removeAll();
                bc.delete(0, bc.length());
            }
            return true;
        }
        return false;
    }
}
