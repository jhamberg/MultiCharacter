package hamberg;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class JoinListener implements Listener {
    private final Plugin plugin;

    public JoinListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String profile = ActiveProfileManager.getInstance().getActiveProfile(plugin, player);
        if (player.getName().equalsIgnoreCase(profile)) {
            player.setDisplayName(player.getName());
        } else {
            player.setDisplayName(player.getName() + " (" + profile + ")");
        }

        event.setJoinMessage("Terveppä terve " + profile);
    }
}
