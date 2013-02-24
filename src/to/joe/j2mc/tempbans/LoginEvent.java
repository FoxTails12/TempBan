package to.joe.j2mc.tempbans;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat.Field;
import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class LoginEvent implements Listener{

	private TempBans main = new TempBans();
	private PlayerLoginEvent login = null;
	
	@EventHandler(priority = EventPriority.HIGH)
	public void highLogin(PlayerLoginEvent event) {
		main.log(Level.INFO, event.getPlayer().getName() + " has connected!");
		login = event;
		Player player = event.getPlayer();
		if(isBanned(player)) {
			bannedPlayer(player);
		}
	}
	
	private boolean isBanned(Player player) {
		FileConfiguration ban = YamlConfiguration.loadConfiguration(new File("tempbans.yml"));
		if(ban.contains(player.getName())) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("null")
	private void bannedPlayer(Player player) {
		FileConfiguration ban = YamlConfiguration.loadConfiguration(new File("tempbans.yml"));
		String p = player.getName();
		
		Calendar now = Calendar.getInstance();
		Calendar date = null;
		
		String banreason = ban.getString(p + ".banreason");
		
		String unban = ban.getString(p + ".banreason");
		
		date.set(Field.YEAR.getCalendarField(), ban.getInt(p + ".unban.year"));
		date.set(Field.MONTH.getCalendarField(), ban.getInt(p + ".unban.month"));
		date.set(Field.WEEK_OF_YEAR.getCalendarField(), ban.getInt(p + ".unban.week"));
		date.set(Field.DAY_OF_YEAR.getCalendarField(), ban.getInt(p + ".unban.day"));
		date.set(Field.HOUR_OF_DAY0.getCalendarField(), ban.getInt(p + ".unban.hour"));
		date.set(Field.MINUTE.getCalendarField(), ban.getInt(p + ".unban.minute"));
		
		if(now.after(date)) {
			main.log(Level.INFO, player.getName() + " is no longer banned.");
			File banfile = new File("tempbans.yml");
			FileConfiguration banf = YamlConfiguration.loadConfiguration(banfile);
			banf.set(player.getName(), null);
			try {
				ban.save(banfile);
				main.log(Level.INFO, "Sucsessfuly saved banfile to disk");
			} catch (IOException e) {
				main.log(Level.SEVERE, "Failed to save banfile to disk");
				e.printStackTrace();
			}
		}
		else {
			String line1 = "You are " + ChatColor.BOLD + ChatColor.DARK_RED + "BANNED" + ChatColor.RESET + ChatColor.WHITE + " from this server.";
			String line2 = "You were banned for:" + ChatColor.GREEN + banreason + ChatColor.WHITE;
			String line3 = "This ban expires on: " + unban;
			String line4 = "Appeal at " + ChatColor.GREEN + "www.joe.to/unban/";
			
			String kickmessage = line1 + "\n" +  line2 + "\n" + line3 + "\n" + line4;
			
			login.disallow(Result.KICK_OTHER, kickmessage);
		}
	}
}
