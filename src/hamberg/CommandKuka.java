package hamberg;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandKuka implements CommandExecutor {
    private final Plugin plugin;

    public CommandKuka(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player) || strings.length > 0) {
            return false;
        }

        Player player = (Player) sender;
        String active = ActiveProfileManager.getInstance().getActiveProfile(plugin, player);
        player.sendMessage("Olet " + active);

        return true;
    }
}
