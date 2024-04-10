package io.papermc.mitaine.mairie;

import io.papermc.mitaine.MitaineMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Mairie implements CommandExecutor, TabCompleter {
    private final MitaineMain main;

    public Mairie(MitaineMain mitaineMain) {
        this.main = mitaineMain;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {

        if (sender instanceof Player player) {
            FileConfiguration config = main.getConfig();
            UUID pId = player.getUniqueId();

            if (pId.toString().equalsIgnoreCase(config.getString("mairie.maire")) || Bukkit.getOperators().contains(player)) {
                if (cmd.getName().equalsIgnoreCase("mairie")) {
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("banque") || args[0].equalsIgnoreCase("bank") || args[0].equalsIgnoreCase("solde")) {
                            player.sendMessage(config.getString("titre") + " La mairie a " + config.getString("important") + config.getInt("mairie.banque") + config.getString("normal") + " diamants en banque.");

                        } else if (args[0].equalsIgnoreCase("retirer") || args[0].equalsIgnoreCase("pull")) {
                            if (args.length != 2) {
                                player.sendMessage(config.getString("erreur") + "la commande est /mairie retirer <montant>");
                            } else if (player.getInventory().firstEmpty() == -1) {
                                player.sendMessage(config.getString("erreur") + "vous n'avez plus de place dans votre inventaire");
                            } else {
                                try {
                                    int nb = Integer.parseInt(args[1]); //test si c'est un chiffre
                                    if (nb > 0) {
                                        if (config.getInt("mairie.banque", config.getInt("mairie.banque")) >= nb) {
                                            player.getInventory().addItem(new ItemStack(Material.DIAMOND, nb));
                                            config.set("mairie.banque", config.getInt("mairie.banque") - nb);
                                            main.saveConfig();
                                            sender.sendMessage("Vous avez bien retiré " + config.getString("important") + args[1] + config.getString("normal") + " diamants");
                                        } else {
                                            player.sendMessage(config.getString("erreur") + "La mairie pas assez de diamants en banque");
                                        }
                                    } else {
                                        player.sendMessage(config.getString("erreur") + "Vous ne pouvez pas retirer un nombre négatif (petit malin)");
                                    }
                                } catch (NumberFormatException e) {
                                    player.sendMessage(config.getString("erreur") + "vous devez entrer un chiffre en paramètre");
                                }
                            }

                        } else if (args[0].equalsIgnoreCase("deposer") || args[0].equalsIgnoreCase("push")) {
                            if (args.length != 2) {
                                player.sendMessage(config.getString("erreur") + "la commande est /mairie deposer <montant>");
                            } else {
                                try {
                                    int nb = Integer.parseInt(args[1]); //test si c'est un chiffre
                                    if (nb > 0) {
                                        if (player.getInventory().contains(Material.DIAMOND, nb)) {
                                            for (int i = 0; i < nb; i++) {
                                                player.getInventory().removeItemAnySlot(new ItemStack(Material.DIAMOND));
                                            }
                                            config.set("mairie.banque", config.getInt("mairie.banque") + nb);
                                            main.saveConfig();
                                            sender.sendMessage("Vous avez bien déposé " + config.getString("important") + args[1] + config.getString("normal") + " diamants");
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

                        } else if (args[0].equalsIgnoreCase("setmaire")) {
                            if (args.length == 2) {
                                try {
                                    UUID newMaire = Bukkit.getPlayerUniqueId(args[1]);
                                    assert newMaire != null;
                                    config.set("mairie.maire", newMaire.toString());
                                    player.sendMessage("Le nouveau maire " + config.getString("important") + args[1] + config.getString("normal") + " a été défini");
                                    main.saveConfig();
                                } catch (Exception e) {
                                    player.sendMessage(config.getString("erreur") + "Le joueur n'existe pas");
                                }
                            } else {
                                player.sendMessage(config.getString("erreur") + "La commande est /mairie setmaire <Joueur>");
                            }
                        } else {
                            player.sendMessage(config.getString("erreur") + "La commande est /mairie <banque | retirer | deposer | setmaire>");
                        }
                    } else {
                        player.sendMessage(config.getString("erreur") + "La commande est /mairie <banque | retirer | deposer | setmaire>");
                    }
                }
            } else {
                player.sendMessage(config.getString("erreur") + "Vous n'êtes pas maire");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command
            cmd, @NotNull String msg, String[] args) {
        return null;
    }
}