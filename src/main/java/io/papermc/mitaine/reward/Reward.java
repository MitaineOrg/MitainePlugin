package io.papermc.mitaine.reward;

import io.papermc.mitaine.MitaineMain;
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
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Reward implements CommandExecutor, Listener, TabCompleter {
    private final MitaineMain main;

    public Reward(MitaineMain mitaineMain) {
        this.main = mitaineMain;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent join) {
        Player player = join.getPlayer();
        FileConfiguration config = main.getConfig();
        String date = config.getString(player.getUniqueId() + ".reward.date");
        if (date == null || !date.equalsIgnoreCase(LocalDate.now().toString())) {
            player.sendMessage("Vous n'avez pas récupéré votre récompense journalière : faites " + config.getString("important") + "/reward");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {

        if (sender instanceof Player player) {
            UUID pId = player.getUniqueId();
            FileConfiguration config = main.getConfig();
            if (cmd.getName().equalsIgnoreCase("reward")) {
                String dateReward = config.getString(pId + ".reward.date");
                LocalDate dateNow = LocalDate.now();
                if (dateReward == null) {
                    config.set(pId + ".reward.streak", 0);
                    config.set(pId + ".reward.date", dateNow.toString());
                    main.saveConfig();
                    getReward(player, config);
                }
                else if (dateNow.equals(LocalDate.parse(dateReward))) {
                    player.sendMessage(config.getString("erreur") + "Vous avez déjà récupéré votre récompense aujourd'hui");
                } else {
                    if (!dateNow.minusDays(1).toString().equalsIgnoreCase(dateReward)) {
                        config.set(pId + ".reward.streak", 0);
                        main.getLogger().info("test");
                        main.saveConfig();
                    }
                    config.set(pId + ".reward.date", dateNow.toString());
                    getReward(player, config);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        return null;
    }

    public void getReward(Player player, FileConfiguration config) {
        UUID pId = player.getUniqueId();
        String streak = config.getString(pId + ".reward.streak");
        assert streak != null;
        int nb = Integer.parseInt(streak);
        if (nb >= 5) {
            nb = 5;
        } else {
            nb += 1;
        }
        config.set(pId + ".reward.streak", nb);
        config.set(pId + ".banque", config.getInt(pId + ".banque") + nb);
        main.saveConfig();
        String pluriel = " diamant";
        if (nb != 1) {
            pluriel += "s";
        }
        player.sendMessage("Vous avez bien récupéré votre récompense de " + config.getString("important") + nb + config.getString("normal") + pluriel);
    }
}
