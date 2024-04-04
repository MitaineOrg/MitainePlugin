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

        Bukkit.getLogger().info("Merci d'utiliser Mitaine Economy");

        //Courrier
        getCommand("courrier").setExecutor(new Courrier(this));
        getServer().getPluginManager().registerEvents(new Courrier(this), this);


        //Economie
        getCommand("banque").setExecutor(new Banque(this));
        getCommand("donner").setExecutor(new Banque(this));
        getCommand("retirer").setExecutor(new Banque(this));
        getCommand("deposer").setExecutor(new Banque(this));
        getServer().getPluginManager().registerEvents(new Banque(this), this);

        getCommand("icone").setExecutor(new Achat(this));
        getCommand("leaderboard").setExecutor(new Leaderboard(this));


        //Mairie
        getCommand("mairie").setExecutor(new Mairie(this));


        //Reward
        getCommand("reward").setExecutor(new Reward(this));
        getServer().getPluginManager().registerEvents(new Reward(this), this);


        //Teleport
        getCommand("setspawn").setExecutor(new Teleport(this));
        getCommand("spawn").setExecutor(new Teleport(this));
        getCommand("sethome").setExecutor(new Teleport(this));
        getCommand("home").setExecutor(new Teleport(this));


        //Vote
        getCommand("vote").setExecutor(new Vote(this));
        getCommand("creervote").setExecutor(new Vote(this));
        getCommand("resultats").setExecutor(new Vote(this));
        getServer().getPluginManager().registerEvents(new Vote(this), this);

        //Mitaine
        getCommand("aide").setExecutor(new Aide(this));
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Au revoir");
    }
}
