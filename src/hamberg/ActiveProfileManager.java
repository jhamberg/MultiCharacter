package hamberg;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ActiveProfileManager {
    private static ActiveProfileManager instance;
    private Map<String, String> active = new HashMap<>();

    public static ActiveProfileManager getInstance() {
        if (instance == null) {
            instance = new ActiveProfileManager();
        }
        return instance;
    }

    private void loadActiveProfile(Plugin plugin, Player player) {
        String uuid = player.getUniqueId().toString();
        String name;
        File file = new File(plugin.getDataFolder().getAbsolutePath(),uuid + ".active.yml");
        if (!file.exists()) {
            name = player.getName();
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            name = (String) config.get("name");
        }
        setActiveProfile(plugin, player, name);
    }

    public String getActiveProfile(Plugin plugin, Player player) {
        String uuid = player.getUniqueId().toString();
        if (!active.containsKey(uuid)) {
            loadActiveProfile(plugin, player);
        }
        return active.get(uuid);
    }

    public void setActiveProfile(Plugin plugin, Player player, String name) {
        active.put(player.getUniqueId().toString(), name);

        // Save to file
        String uuid = player.getUniqueId().toString();
        File file = new File(plugin.getDataFolder().getAbsolutePath(),uuid + ".active.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("name", name);
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
