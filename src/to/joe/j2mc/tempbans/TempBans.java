package to.joe.j2mc.tempbans;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.tempbans.commands.PardonCommand;
import to.joe.j2mc.tempbans.commands.TempbanCommand;

public class TempBans extends JavaPlugin{

	public void onEnable() {
		this.getLogger().log(Level.INFO, "Loading TempBans...");
		getCommand("tban").setExecutor(new TempbanCommand(this));
		getCommand("tpardon").setExecutor(new PardonCommand(this));
		getServer().getPluginManager().registerEvents(new LoginEvent(), this);
		this.getLogger().log(Level.INFO, "Loaded.");
	}
	
	public void onDisable() {
		this.getLogger().log(Level.INFO, "TempBans diabled.");
	}
	
	public void log(Level loglevel, String msg) {
		this.getLogger().log(loglevel, msg);
	}
}
