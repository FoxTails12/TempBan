package to.joe.j2mc.tempbans.commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import to.joe.j2mc.tempbans.TempBans;

public class PardonCommand implements CommandExecutor{

	private TempBans main = new TempBans();
	
	public PardonCommand(TempBans tempBans) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.hasPermission("permission.to.pardon")) {
				if(args.length == 1) {
					if(playerBanned(args[0])) {
						pardon(args[0], player);
					}
					else {
						player.sendMessage(ChatColor.RED + "\"" + args[0] + "\" is not banned");
					}
				}
				else {
					player.sendMessage(ChatColor.RED + "Invalid number of arguments. Use: /tpardon player");
				}
			}
			else {
				player.sendMessage(ChatColor.RED + "You don't have permission to do that");
			}
		}
		else {
			//TODO: implement console execution
		}
		return false;
	}
	
	private boolean playerBanned(String target) {
		FileConfiguration banfile = YamlConfiguration.loadConfiguration(new File("tempbans.yml"));
		if(banfile.contains(target)) { 
			return true; 
		}
		return false;
	}
	
	private void pardon(String player, Player sender) {
		main.log(Level.INFO, "Pardoning " + player + "...");
		File banfile = new File("tempbans.yml");
		FileConfiguration ban = YamlConfiguration.loadConfiguration(banfile);
		ban.set(player, null);
		try {
			ban.save(banfile);
			main.log(Level.INFO, "Sucsessfuly saved banfile to disk");
			sender.sendMessage(ChatColor.GREEN + "Sucsessfuly pardoned " + player);
		} catch (IOException e) {
			main.log(Level.SEVERE, "Failed to save banfile to disk");
			e.printStackTrace();
		}
	}

}
