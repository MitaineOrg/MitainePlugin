package io.papermc.mitaine.teleport;

import io.papermc.mitaine.MitaineMain;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.UUID;

public class Teleport extends BukkitRunnable implements CommandExecutor, Listener {
    private final MitaineMain main;
    private ArrayList<TeleportItem> teleportQueue;

    public Teleport(MitaineMain mitaineMain) {
        this.main = mitaineMain;
        this.teleportQueue = new ArrayList<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {

        if (sender instanceof Player player) {

            String command = cmd.getName().toLowerCase();
            FileConfiguration config = main.getConfig();

            if (command.equals("setspawn")) {
                if (args.length == 0) {
                    setTeleport(player, config, "spawnPoint");
                } else {
                    player.sendMessage(config.getString("erreur") + "Faites /setspawn");
                }
                return true;

            } else if (command.equals("spawn")) {
                if (args.length == 0) {
                    enqueueTeleport(player, "spawnPoint");
                } else {
                    player.sendMessage(config.getString("erreur") + "Faites /spawn");
                }
                return true;

            } else if (command.equals("delspawn")) {
                delTeleport(player, "spawnPoint");
            } else if (command.equals("sethome")) {
                if (args.length == 1) {
                    setTeleport(player, config, player.getUniqueId() + ".teleports." + args[0]);
                } else {
                    player.sendMessage(config.getString("erreur") + "Faites /sethome <nom>");
                }
                return true;

            } else if (command.equals("home")) {
                if (args.length == 1) {
                    enqueueTeleport(player, player.getUniqueId() + ".teleports." + args[0]);
                } else {
                    player.sendMessage(config.getString("erreur") + "Faites /home <nom>");
                }
                return true;
            } else if (command.equals("delhome")) {
                if (args.length == 1) {
                    delTeleport(player, player.getUniqueId() + ".teleports." + args[0]);
                } else {
                    player.sendMessage(config.getString("erreur") + "Faites /delhome <nom>");
                }
            }
        }
        return false;
    }

    private void enqueueTeleport(Player player, String emplacement) {
        UUID pId = player.getUniqueId();
        FileConfiguration config = main.getConfig();

        if (config.getString(emplacement + ".world") == null) {
            player.sendMessage(config.getString("erreur") + "Le point de téléportation n'existe pas");
            return;
        }

        if (!player.getWorld().getName().equalsIgnoreCase(config.getString(emplacement + ".world"))) {
            player.sendMessage(config.getString("erreur") + "La commande ne peut se faire que dans le monde du point de téléportation");
            return;
        }

        Location tp = new Location(player.getWorld(), config.getInt(emplacement + ".x"), config.getInt(emplacement + ".y"), config.getInt(emplacement + ".z"));

        double distance = sqrt(pow(player.getLocation().getBlockX() - tp.getBlockX(), 2) + pow(player.getLocation().getBlockZ() - tp.getBlockZ(), 2));
        int prix = (int) round(sqrt(distance) / 10 + 1);
        if (player.getLocation().getWorld().getName().equalsIgnoreCase("world_nether")) {
            prix *= 8;
        }
        player.sendMessage("Vous êtes à " + config.getString("important") + round(distance) + config.getString("normal") + " blocs du point, cela vous coûtera " + config.getString("important") + prix + config.getString("normal") + " diamants");

        if (config.getInt(pId + ".banque") >= prix) {
            player.sendMessage("Vous allez etre téléporté dans 5 secondes, ne bougez pas...");
            teleportQueue.add(new TeleportItem(player, player.getLocation(), 100, emplacement, prix));
        } else {
            player.sendMessage(config.getString("erreur") + "Vous n'avez pas assez de diamants en banque");
        }
    }

    private void delTeleport(Player player, String emplacement) {
        FileConfiguration config = main.getConfig();
        if (config.getString(emplacement + ".world") == null) {
            player.sendMessage(config.getString("erreur") + "Le point de téléportaion n'existe pas");
        } else {
            config.set(emplacement, null);
            main.saveConfig();
            player.sendMessage("Le point de téléportation a bien été supprimé");
        }
    }

    @Override
    public void run() {
        FileConfiguration config = main.getConfig();
        ArrayList<TeleportItem> newQueue = new ArrayList<>();
        for (TeleportItem item : teleportQueue) {

            Player player = item.player;
            UUID pId = player.getUniqueId();
            String emplacement = item.emplacement;
            int prix = item.prix;

            if (item.location.distance(player.getLocation()) > 0.1) {
                player.sendMessage(config.getString("erreur") + "Téléportaion annulée >:(");
                continue;
            }

            if (item.ticksLeft <= 0) {
                player.teleport(new Location(player.getWorld(), config.getInt(emplacement + ".x"), config.getInt(emplacement + ".y"), config.getInt(emplacement + ".z")));
                config.set("mairie.banque", config.getInt("mairie.banque") + prix);
                config.set(pId + ".banque", config.getInt(pId + ".banque") - prix);
                main.saveConfig();
                player.sendMessage("Vous avez bien été téléporté !");
            } else {
                item.ticksLeft--;
                newQueue.add(item);
            }
        }
        this.teleportQueue = newQueue;
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

    private static class TeleportItem {
        public Player player;
        public Location location;
        public int ticksLeft;
        public String emplacement;
        public int prix;

        public TeleportItem(Player player, Location location, int ticksLeft, String emplacement, int prix) {
            this.player = player;
            this.location = location;
            this.ticksLeft = ticksLeft;
            this.emplacement = emplacement;
            this.prix = prix;
        }

        @Override
        public String toString() {
            return player.displayName() + ": " + location.toString() + " " + emplacement;
        }
    }
}
