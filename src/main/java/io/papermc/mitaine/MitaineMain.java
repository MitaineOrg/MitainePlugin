package io.papermc.mitaine;

import io.papermc.mitaine.courrier.Courrier;
import io.papermc.mitaine.economie.Achat;
import io.papermc.mitaine.economie.Banque;
import io.papermc.mitaine.economie.Leaderboard;
import io.papermc.mitaine.mairie.Mairie;
import io.papermc.mitaine.reward.Reward;
import io.papermc.mitaine.teleport.Teleport;
import io.papermc.mitaine.vote.Vote;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MitaineMain extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getLogger().info("Merci d'utiliser Mitaine Plugin");

        //Mitaine
        Aide aide = new Aide(this);
        getCommand("aide").setExecutor(aide);
        getServer().getPluginManager().registerEvents(aide, this);

        //Courrier
        Courrier courrier = new Courrier(this);
        getCommand("courrier").setExecutor(courrier);
        getServer().getPluginManager().registerEvents(courrier, this);

        //Economie
        Banque banque = new Banque(this);
        getCommand("banque").setExecutor(banque);
        getCommand("donner").setExecutor(banque);
        getCommand("retirer").setExecutor(banque);
        getCommand("deposer").setExecutor(banque);

        Achat achat = new Achat(this);
        getCommand("icone").setExecutor(achat);

        Leaderboard leaderboard = new Leaderboard(this);
        getCommand("leaderboard").setExecutor(leaderboard);

        Mairie mairie = new Mairie(this);
        getCommand("mairie").setExecutor(mairie);

        //Reward
        Reward reward = new Reward(this);
        getCommand("reward").setExecutor(reward);
        getServer().getPluginManager().registerEvents(reward, this);

        //Teleport
        Teleport teleport = new Teleport(this);
        getCommand("setspawn").setExecutor(teleport);
        getCommand("spawn").setExecutor(teleport);
        getCommand("sethome").setExecutor(teleport);
        getCommand("home").setExecutor(teleport);
        getCommand("homelist").setExecutor(teleport);
        getCommand("delhome").setExecutor(teleport);
        getCommand("delspawn").setExecutor(teleport);
        teleport.runTaskTimer(this, 0L, 1L);

        //Vote
        Vote vote = new Vote(this);
        getCommand("vote").setExecutor(vote);
        getCommand("creervote").setExecutor(vote);
        getCommand("resultats").setExecutor(vote);
        getServer().getPluginManager().registerEvents(vote, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Au revoir");
    }
}
