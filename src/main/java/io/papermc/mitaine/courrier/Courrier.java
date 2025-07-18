package io.papermc.mitaine.courrier;

import io.papermc.mitaine.MitaineMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Courrier implements CommandExecutor, Listener, TabCompleter {
    private final MitaineMain main;

    public Courrier(MitaineMain mitaineMain) {
        this.main = mitaineMain;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent join) {
        Player player = join.getPlayer();
        FileConfiguration config = main.getConfig();
        String nombre = config.getString(player.getUniqueId() + ".courriers.nombre");
        if (nombre == null) {
            config.set(player.getUniqueId() + ".courriers.nombre", 0);
            main.saveConfig();
        } else {
            try {
                if (Integer.parseInt(nombre) >= 1) {
                    player.sendMessage("Vous avez " + config.getString("important") + config.getString(player.getUniqueId() + ".courriers.nombre") + config.getString("normal") + " courriers en attente");
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {

        if (cmd.getName().equalsIgnoreCase("courrier")) {
            FileConfiguration config = main.getConfig();
            if (args.length != 0) {

                if (args[0].equalsIgnoreCase("envoyer")) {
                    if (args.length >= 3) {
                        StringBuilder contenu = new StringBuilder();
                        UUID reciever = Bukkit.getPlayerUniqueId(args[1]);
                        if (reciever == null) {
                            sender.sendMessage(config.getString("erreur") + " Le joueur renseigné n'existe pas");
                            return false;
                        }
                        for (int i = 2; i < args.length; i++) {
                            contenu.append(args[i]).append(" ");
                        }
                        String nameSender;
                        String nombre = config.getString(reciever + ".courriers.nombre");
                        int nbMsg = 1;
                        if (nombre != null) {
                            nbMsg += Integer.parseInt(nombre);
                        }
                        if (sender instanceof Player player) {
                            nameSender = player.getName();
                            config.set(reciever + ".courriers." + nbMsg + ".idSender", player.getUniqueId().toString());
                        } else {
                            nameSender = config.getString("titre") + config.getString("important") + " Info";
                        }
                        config.set(reciever + ".courriers.nombre", nbMsg);
                        config.set(reciever + ".courriers." + nbMsg + ".date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy à HH:mm:ss")));
                        config.set(reciever + ".courriers." + nbMsg + ".sender", nameSender);
                        config.set(reciever + ".courriers." + nbMsg + ".message", contenu.toString());
                        main.saveConfig();
                        try {
                            Objects.requireNonNull(Bukkit.getPlayer(reciever)).sendMessage(config.getString("titre") + " Vous avez reçu un message !");
                            sender.sendMessage("Le courrier a bien été envoyé à " + config.getString("important") + args[1]);
                        } catch (NullPointerException e) {
                            sender.sendMessage("Le joueur n'est pas en ligne, il verra le message à la prochaine connexion");
                        }
                    } else {
                        sender.sendMessage(config.getString("erreur") + "La commande est /courrier envoyer <destinataire> <message>");
                    }
                    return true;

                } else if (sender instanceof Player player) {
                    if (args[0].equalsIgnoreCase("liste")) {
                        if (args.length == 1) {
                            UUID idPlayer = player.getUniqueId();
                            int nbMsg = config.getInt(idPlayer + ".courriers.nombre");
                            if (nbMsg == 0) {
                                player.sendMessage("Vous n'avez pas de nouveaux messages");
                            }
                            for (int i = 1; i <= nbMsg; i++) {
                                String message = config.getString("discret") +
                                        i + config.getString("normal") +
                                        " - Reçu le " +
                                        config.getString("discret") +
                                        config.getString(idPlayer + ".courriers." + i + ".date") +
                                        config.getString("normal") +
                                        " par " +
                                        config.getString("important") +
                                        config.getString(idPlayer + ".courriers." + i + ".sender");
                                TextComponent supprimer = Component.text(config.getString("erreur") + "   | Supprimer |");
                                supprimer = supprimer.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Supprimer le message")));
                                supprimer = supprimer.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/courrier supprimer " + i));
                                TextComponent action = Component.text(config.getString("valider") + "   | Lire |");
                                action = action.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Lire le message")));
                                action = action.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/courrier lire " + i));
                                action = action.append(supprimer);
                                player.sendMessage(message);
                                player.sendMessage(action);
                                player.sendMessage(config.getString("discret") + "-----------------------------------");
                            }
                            TextComponent action = Component.text(config.getString("important") + "   | Rédiger |");
                            action = action.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Nouveau message")));
                            action = action.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/courrier envoyer "));
                            player.sendMessage(action);
                        } else {
                            player.sendMessage(config.getString("erreur") + "La commande est /courrier liste");
                        }
                        return true;

                    } else if (args[0].equalsIgnoreCase("lire")) {
                        int nbMsg = config.getInt(player.getUniqueId() + ".courriers.nombre");
                        try {
                            int entree = Integer.parseInt(args[1]);

                            if (args.length == 2 && entree <= nbMsg && entree > 0) {
                                String enTete = player.getUniqueId() + ".courriers." + args[1];
                                String message = config.getString("discret") +
                                        config.getString(enTete + ".date") +
                                        config.getString("normal") +
                                        " - " +
                                        config.getString("important") +
                                        config.getString(enTete + ".sender") +
                                        config.getString("normal") +
                                        "\n" +
                                        config.getString(enTete + ".message");
                                TextComponent retour = Component.text(config.getString("discret") + "   | Retour |");
                                retour = retour.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Revenir à la liste")));
                                retour = retour.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/courrier liste"));
                                TextComponent supprimer = Component.text(config.getString("erreur") + "   | Supprimer |");
                                supprimer = supprimer.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Supprimer le message")));
                                supprimer = supprimer.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/courrier supprimer " + entree));
                                TextComponent action = Component.text("");
                                if (!Objects.requireNonNull(config.getString(enTete + ".sender")).equalsIgnoreCase(config.getString("titre") + config.getString("important") + " Admin")) {
                                    action = Component.text(config.getString("valider") + "   | Répondre |");
                                    action = action.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Répondre au message")));
                                    action = action.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/courrier envoyer " + config.getString(enTete + ".sender") + " "));
                                }
                                action = action.append(supprimer).append(retour);
                                player.sendMessage(message);
                                player.sendMessage(action);

                                String idSender = config.getString(enTete + ".idSender");
                                if (idSender != null) {
                                    String nombre = config.getString(config.getString(enTete + ".idSender") + ".courriers.nombre");
                                    nbMsg = 1;
                                    if (nombre != null) {
                                        nbMsg += Integer.parseInt(nombre);
                                    }
                                    config.set(idSender + ".courriers.nombre", nbMsg);
                                    config.set(idSender + ".courriers." + nbMsg + ".date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy à HH:mm:ss")));
                                    config.set(idSender + ".courriers." + nbMsg + ".sender", config.getString("titre") + config.getString("important") + " Admin");
                                    config.set(idSender + ".courriers." + nbMsg + ".idSender", null);
                                    config.set(idSender + ".courriers." + nbMsg + ".message", "Le message envoyé à " + config.getString("important") + player.getName() + config.getString("normal") + " a bien été ouvert.");
                                    try {
                                        Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(idSender))).sendMessage(config.getString("titre") + " Vous avez reçu un message !");
                                    } catch (NullPointerException ignored) {
                                    }
                                    config.set(enTete + ".idSender", null);
                                    main.saveConfig();
                                }
                            } else {
                                player.sendMessage(config.getString("erreur") + "La commande est /courrier lire <nombre>");
                            }
                        } catch (Exception e) {
                            player.sendMessage(config.getString("erreur") + "La commande est /courrier lire <nombre>");
                        }
                        return true;

                    } else if (args[0].equalsIgnoreCase("supprimer")) {
                        UUID pId = player.getUniqueId();

                        int nbMsg = config.getInt(pId + ".courriers.nombre");
                        int entree = 0;
                        try {
                            entree = Integer.parseInt(args[1]);
                        } catch (Exception e) {
                            player.sendMessage(config.getString("erreur") + "La commande est /courrier supprimer <nombre>");
                        }
                        if (args.length == 2 && entree <= nbMsg && entree > 0) {
                            for (int i = entree; i <= nbMsg; i++) {
                                config.set(pId + ".courriers." + i + ".date", config.getString(pId + ".courriers." + (i + 1) + ".date"));
                                config.set(pId + ".courriers." + i + ".sender", config.getString(pId + ".courriers." + (i + 1) + ".sender"));
                                config.set(pId + ".courriers." + i + ".idSender", config.getString(pId + ".courriers." + (i + 1) + ".idSender"));
                                config.set(pId + ".courriers." + i + ".message", config.getString(pId + ".courriers." + (i + 1) + ".message"));
                            }
                            config.set(pId + ".courriers." + nbMsg, null);
                            config.set(pId + ".courriers.nombre", nbMsg - 1);
                            main.saveConfig();
                            player.sendMessage("Message Supprimé");
                            player.performCommand("courrier liste");
                        } else {
                            player.sendMessage(config.getString("erreur") + "La commande est /courrier supprimer <nombre>");
                        }
                        return true;
                    } else {
                        sender.sendMessage(config.getString("erreur") + "Les options sont : /courrier <liste | envoyer | lire | supprimer>");
                    }
                } else {
                    sender.sendMessage("La console n'a pas de boite aux lettres");
                }
            } else if (sender instanceof Player player) {
                player.performCommand("courrier liste");
            } else {
                sender.sendMessage(config.getString("erreur") + "La commande est /courrier <option>");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            List<String> completions = new ArrayList<>();
            completions.add("liste");
            completions.add("envoyer");
            completions.add("lire");
            completions.add("supprimer");
            return completions;
        }
        return null;
    }
}
