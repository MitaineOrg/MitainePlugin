package io.papermc.mitaine.economie;

import io.papermc.mitaine.MitaineMain;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Economie implements CommandExecutor {
    private final MitaineMain main;

    public Economie(MitaineMain mitaineMain) {
        this.main = mitaineMain;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent join) {
        Player player = join.getPlayer();
        main.reloadConfig();
        FileConfiguration config = main.getConfig();
        if (config.getString(player.getUniqueId() + ".banque") != null) {
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
                    player.sendMessage(config.getString("erreur") + "la commande est /economie retirer <chiffre>");
                } else if (player.getInventory().firstEmpty() == -1) {
                    //faire le test des diamants dispos
                    player.sendMessage(config.getString("erreur") + "vous n'avez plus de place dans votre inventaire");
                } else {
                    try {
                        int nb = Integer.parseInt(args[0]); //test si c'est un chiffre
                        player.getInventory().addItem(new ItemStack(Material.DIAMOND, nb));
                        config.set(pId + ".banque", config.getInt(pId + ".banque") - nb);
                        main.saveConfig();
                    } catch (NumberFormatException e) {
                        player.sendMessage(config.getString("erreur") + "vous devez entrer un chiffre en paramètre");
                    }
                }
                return true;

            } else if (cmd.getName().equalsIgnoreCase("donner")) {
                if (args.length != 1) {
                    player.sendMessage(config.getString("erreur") + "la commande est /economie donner <chiffre>");
                } else {
                    try {
                        int nb = Integer.parseInt(args[0]); //test si c'est un chiffre
                        if (player.getInventory().contains(new ItemStack(Material.DIAMOND, nb))) {
                            player.getInventory().remove(new ItemStack(Material.DIAMOND, nb));
                            config.set(pId + ".banque", config.getInt(pId + ".banque") + nb);
                            main.saveConfig();
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
