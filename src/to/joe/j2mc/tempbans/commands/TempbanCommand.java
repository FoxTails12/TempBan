package to.joe.j2mc.tempbans.commands;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat.Field;
import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import to.joe.j2mc.tempbans.TempBans;

public class TempbanCommand implements CommandExecutor{

	String bantime = null;
	String bantimeplain = null;
	
	private TempBans main = new TempBans();
	
	public TempbanCommand(TempBans tempBans) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.hasPermission("has.permission.to.tempban")) {  //TODO: Get actual permission name
				if(args.length == 2 || args.length == 3) {
					if(properTimeFormat(args[1])) {
						if(Bukkit.getPlayer(args[0]) != null) {
							Player target = Bukkit.getPlayer(args[0]);
							this.smitePlayer(target);
							this.sendKickMessage(target, getBanReason(args[2]));
						}
						this.broadcastBan(args[0], sender.getName(), getBanReason(args[2]));
						writeBan(args[0], getUnbanDate(args[1]), player, getBanReason(args[2]));
					}
					else {
						player.sendMessage("Invalid time format.");
					}
				}
				else {
					player.sendMessage(ChatColor.RED + "Invalid number of arguments. Use: /tban player time reason");
				}
			}
			else {
				player.sendMessage(ChatColor.RED + "You don't have permission to do that");
			}
		}
		else {
			//TODO: Handle console input
		}
		
		return false;
	}
	
	private boolean properTimeFormat(String time) {
		if(time.endsWith("m") || time.endsWith("h") || time.endsWith("d") || time.endsWith("w") || time.endsWith("mo") || time.endsWith("y")) {
			return true;
		}
		return false;
	}
	
	private Calendar getUnbanDate(String input) {
		Calendar time = Calendar.getInstance();
		if(input.endsWith("m")) {
			int num = Integer.getInteger(input.split("m")[0]);
			time.add(Field.MINUTE.getCalendarField(), num);
			if(num == 1) {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " minute";
				bantimeplain = num + " minute";
			}
			else {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " minutes";
				bantimeplain = num + " minutes";
			}
		}
		if(input.endsWith("h")) {
			int num = Integer.getInteger(input.split("h")[0]);
			time.add(Field.HOUR0.getCalendarField(), num);
			if(num == 1) {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " hour";
				bantimeplain = num + " hour";
			}
			else {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " hours";
				bantimeplain = num + " hours";
			}
		}
		if(input.endsWith("d")) {
			int num = Integer.getInteger(input.split("d")[0]);
			time.add(Field.DAY_OF_YEAR.getCalendarField(), num);
			if(num == 1) {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " day";
				bantimeplain = num + " day";
			}
			else {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " days";
				bantimeplain = num + " days";
			}
		}
		if(input.endsWith("w")) {
			int num = Integer.getInteger(input.split("w")[0]);
			time.add(Field.WEEK_OF_YEAR.getCalendarField(), num);
			if(num == 1) {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " week";
				bantimeplain = num + " week";
			}
			else {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " weeks";
				bantimeplain = num + " weeks";
			}
		}
		if(input.endsWith("mo")) {
			int num = Integer.getInteger(input.split("mo")[0]);
			time.add(Field.MONTH.getCalendarField(), num);
			if(num == 1) {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " month";
				bantimeplain = num + " month";
			}
			else {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " months";
				bantimeplain = num + " months";
			}
		}
		if(input.endsWith("y")) {
			int num = Integer.getInteger(input.split("y")[0]);
			time.add(Field.YEAR.getCalendarField(), num);
			if(num == 1) {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " year";
				bantimeplain = num + " year";
			}
			else {
				bantime = ChatColor.GREEN + "" + num + ChatColor.WHITE + " years";
				bantimeplain = num + " years";
			}
		}
		return time;
	}
	
	private void writeBan(String playername, Calendar date, Player sender, String banreason) {
		File banfile = new File("tempbans.yml");
		FileConfiguration banlist = YamlConfiguration.loadConfiguration(banfile);
		banlist.set(playername + ".unban.year", date.get(Field.YEAR.getCalendarField()));
		banlist.set(playername + ".unban.month", date.get(Field.MONTH.getCalendarField()));
		banlist.set(playername + ".unban.week", date.get(Field.WEEK_OF_YEAR.getCalendarField()));
		banlist.set(playername + ".unban.day", date.get(Field.DAY_OF_YEAR.getCalendarField()));
		banlist.set(playername + ".unban.hour", date.get(Field.HOUR_OF_DAY0.getCalendarField()));
		banlist.set(playername + ".unban.minute", date.get(Field.MINUTE.getCalendarField()));
		
		banlist.set(playername + ".unbandate", date.get(Field.MONTH.getCalendarField()) + "/" + date.get(Field.DAY_OF_MONTH.getCalendarField()) + "/" + date.get(Field.YEAR.getCalendarField()) + " at " + date.get(Field.HOUR_OF_DAY0.getCalendarField()) + ":" + date.get(Field.MINUTE.getCalendarField()) + " " + date.get(Field.AM_PM.getCalendarField()));
		
		banlist.set(playername + ".banreason", banreason);
		
		try {
			banlist.save(banfile);
			main.log(Level.INFO, "Sucsessfuly saved banfile to disk");
			sender.sendMessage(ChatColor.GREEN + "Sucsessfuly banned " + playername);
		} catch (IOException e) {
			main.log(Level.SEVERE, "Failed to save banfile to disk");
			e.printStackTrace();
		}
	}
	
	private void sendKickMessage(Player target, String reason) {
		String line1 = "You have been " + ChatColor.BOLD + ChatColor.DARK_RED + "BANNED" + ChatColor.RESET + ChatColor.WHITE;
		String line2 = "You were banned for:" + ChatColor.GREEN + reason + ChatColor.WHITE;
		String line3 = "This ban will last " + bantime;
		String line4 = "Appeal at " + ChatColor.GREEN + "www.joe.to/unban/";
		
		target.kickPlayer(line1 + "\n" +  line2 + "\n" + line3 + "\n" + line4);
	
	}
	
	private void smitePlayer(Player target) {
		Location player = target.getLocation();
		World world = player.getWorld();
		world.strikeLightningEffect(player);
	}
	
	private void broadcastBan(String target, String banner, String reason) {
		for(Player p : Bukkit.getOnlinePlayers()){
			 if(p.hasPermission("j2.administrative.permission.node")) {  //TODO: Get actual permission node name
				 p.sendMessage(ChatColor.RED + banner + " has banned " + target + " for " + reason);
				 p.sendMessage(ChatColor.RED + "Ban length: " + bantimeplain);
			 }
			 else {
				 p.sendMessage(ChatColor.RED + target + " has been banned for " + reason);
			 }
        }
		main.log(Level.INFO, target + " has been temporarily banned by " + banner + " for " + reason);
		main.log(Level.INFO, "This ban will last for " + bantimeplain);
	}
	
	private String getBanReason(String input) {
		if(input != null) {
			return input;
		}
		else {
			return "Banned";
		}
	}

}
