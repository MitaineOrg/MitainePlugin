package io.papermc.mitaine.economie;

import io.papermc.mitaine.MitaineMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Banque implements CommandExecutor, Listener {
    private final MitaineMain main;

    public Banque(MitaineMain mitaineMain) {
        this.main = mitaineMain;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent join) {
        Player player = join.getPlayer();
        main.reloadConfig();
        FileConfiguration config = main.getConfig();
        if (config.getString(player.getUniqueId() + ".banque") == null) {
            config.set(player.getUniqueId() + ".banque", 0);
            main.saveConfig();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {

        if (sender instanceof Player player) {
            FileConfiguration config = main.getConfig();
            UUID pId = player.getUniqueId();

            if (cmd.getName().equalsIgnoreCase("banque")) {
                player.sendMessage(config.getString("titre") + " Vous avez " + config.getString("important") + config.getInt(pId + ".banque") + config.getString("normal") + " diamants en banque.");
                return true;

            } else if (cmd.getName().equalsIgnoreCase("retirer")) {
                if (args.length != 1) {
                    player.sendMessage(config.getString("erreur") + "la commande est /economie retirer <montant>");
                } else if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(config.getString("erreur") + "vous n'avez plus de place dans votre inventaire");
                } else {
                    try {
                        int nb = Integer.parseInt(args[0]); //test si c'est un chiffre
                        if (nb > 0) {
                            if (config.getInt(pId + ".banque", config.getInt(pId + ".banque")) >= nb) {
                                player.getInventory().addItem(new ItemStack(Material.DIAMOND, nb));
                                config.set(pId + ".banque", config.getInt(pId + ".banque") - nb);
                                main.saveConfig();
                                sender.sendMessage("Vous avez bien retiré " + config.getString("important") + args[0] + config.getString("normal") + " diamants");
                            } else {
                                player.sendMessage(config.getString("erreur") + "Vous n'avez pas assez de diamants en banque");
                            }
                        } else {
                            player.sendMessage(config.getString("erreur") + "Vous ne pouvez pas retirer un nombre négatif (petit malin)");
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage(config.getString("erreur") + "vous devez entrer un chiffre en paramètre");
                    }
                }
                return true;

            } else if (cmd.getName().equalsIgnoreCase("deposer")) {
                if (args.length != 1) {
                    player.sendMessage(config.getString("erreur") + "la commande est /economie deposer <montant>");
                } else {
                    try {
                        int nb = Integer.parseInt(args[0]); //test si c'est un chiffre
                        if (nb > 0) {
                            if (player.getInventory().contains(Material.DIAMOND, nb)) {
                                for (int i = 0; i < nb; i++) {
                                    player.getInventory().removeItemAnySlot(new ItemStack(Material.DIAMOND));
                                }
                                config.set(pId + ".banque", config.getInt(pId + ".banque") + nb);
                                main.saveConfig();
                                sender.sendMessage("Vous avez bien déposé " + config.getString("important") + args[0] + config.getString("normal") + " diamants");
                            } else {
                                player.sendMessage(config.getString("erreur") + "Vous n'avez pas assez de diamants dans votre inventaire");
                            }
                        } else {
                            player.sendMessage(config.getString("erreur") + "Vous ne pouvez pas déposer un nombre négatif (c'est un peu con)");
                        }
                    } catch
                    (NumberFormatException e) {
                        player.sendMessage(config.getString("erreur") + "vous devez entrer un chiffre en paramètre");
                    }
                }
                return true;
            } else if (cmd.getName().equalsIgnoreCase("donner")) {
                if (args.length != 2) {
                    player.sendMessage(config.getString("erreur") + "la commande est /economie donner <joueur> <montant>");
                } else {
                    try {
                        int nb = Integer.parseInt(args[1]); //test si c'est un chiffre
                        if (nb > 0) {
                            if (config.getInt(pId + ".banque") >= nb) {
                                UUID reciever = Bukkit.getPlayerUniqueId(args[0]);
                                config.set(pId + ".banque", config.getInt(pId + ".banque") - nb);
                                config.set(reciever + ".banque", config.getInt(reciever + ".banque") + nb);
                                main.saveConfig();
                                sender.sendMessage("Vous avez bien envoyé " + config.getString("important") + args[1] + config.getString("normal") + " diamants à " + config.getString("important") + args[0]);
                            } else {
                                player.sendMessage("Vous n'avez pas assez de diamants sur votre compte");
                            }
                        } else {
                            player.sendMessage(config.getString("erreur") + "Vous ne pouvez pas donner un nombre négatif (espèce de voleur)");
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage(config.getString("erreur") + "vous devez entrer un chiffre en paramètre");
                    }
                }
                return true;
            }
        }
        return false;
    }
}
