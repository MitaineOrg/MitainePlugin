package io.papermc.mitaine.teleport;

import io.papermc.mitaine.MitaineMain;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.*;

import java.util.UUID;

public class Teleport implements CommandExecutor {
    private final MitaineMain main;

    public Teleport(MitaineMain mitaineMain) {
        this.main = mitaineMain;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {

        if (sender instanceof Player player) {

            if (cmd.getName().equalsIgnoreCase("setspawn")) {
                FileConfiguration config = main.getConfig();
                if (args.length == 0) {
                    setTeleport(player, config, "spawnPoint");
                } else {
                    player.sendMessage(config.getString("erreur") + "Faites /setspawn");
                }
                return true;

            } else if (cmd.getName().equalsIgnoreCase("spawn")) {
                FileConfiguration config = main.getConfig();
                if (args.length == 0) {
                    teleport(player, config, "spawnPoint");
                } else {
                    player.sendMessage(config.getString("erreur") + "Faites /spawn");
                }
                return true;

            } else if (cmd.getName().equalsIgnoreCase("sethome")) {
                FileConfiguration config = main.getConfig();
                if (args.length == 1) {
                    setTeleport(player, config, player.getUniqueId() + ".teleports." + args[0]);
                } else {
                    player.sendMessage(config.getString("erreur") + "Faites /sethome <nom>");
                }
                return true;

            } else if (cmd.getName().equalsIgnoreCase("home")) {
                FileConfiguration config = main.getConfig();
                if (args.length == 1) {
                    teleport(player, config, player.getUniqueId() + ".teleports." + args[0]);
                } else {
                    player.sendMessage(config.getString("erreur") + "Faites /home <nom>");
                }
                return true;
            }
        }
        return false;
    }

    private void teleport(Player player, FileConfiguration config, String emplacement) {
        UUID pId = player.getUniqueId();

        if (player.getWorld().getName().equalsIgnoreCase(config.getString(emplacement + ".world"))) {
            double distance = sqrt(pow(player.getLocation().getBlockX() - config.getInt(emplacement + ".x"), 2) + pow(player.getLocation().getBlockZ() - config.getInt(emplacement + ".z"), 2));
            int prix = (int) round(sqrt(distance) / 10 + 1);
            player.sendMessage("Vous êtes à " + config.getString("important") + round(distance) + config.getString("normal") + " blocs du point, cela vous coûtera " + config.getString("important") + prix + config.getString("normal") + " diamants");
            // Faire validation ?
            if (config.getInt(pId + ".banque") >= prix) {
                player.teleport(new Location(player.getWorld(), config.getInt(emplacement + ".x"), config.getInt(emplacement + ".y"), config.getInt(emplacement + ".z")));
                config.set("mairie.banque", config.getInt("mairie.banque") + prix);
                config.set(pId + ".banque", config.getInt(pId + ".banque") - prix);
                main.saveConfig();
                player.sendMessage("Vous avez bien été téléporté !");
            } else {
                player.sendMessage(config.getString("erreur") + "Vous n'avez pas assez de diamants en banque");
            }
        } else if (config.getString(emplacement + ".world") == null) {
            player.sendMessage(config.getString("erreur") + "Le point de téléportation n'existe pas");
        } else {
            player.sendMessage(config.getString("erreur") + "La commande ne peut se faire que dans le monde du point de téléportation");
        }
    }

    private void setTeleport(Player player, FileConfiguration config, String emplacement) {
        if (config.getString(emplacement + ".world") == null) {
            config.set(emplacement + ".world", player.getWorld().getName());
            config.set(emplacement + ".x", player.getLocation().getBlockX());
            config.set(emplacement + ".y", player.getLocation().getBlockY());
            config.set(emplacement + ".z", player.getLocation().getBlockZ());
            main.saveConfig();
            player.sendMessage("Le point de téléportation a bien été défini");
        } else {
            player.sendMessage("Le point a déjà été défini");
            // Faire des trucs cliquables -> redéfinition
        }
    }
}
