package hamberg;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandVaihda implements CommandExecutor {
    private final Plugin plugin;

    public CommandVaihda(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        // Console is not supported
        if (!(sender instanceof Player)) {
            return false;
        }

        // Incorrect amount of arguments
        if (strings.length != 1) {
            return false;
        }

        Player player = (Player) sender;
        String current = ActiveProfileManager.getInstance().getActiveProfile(plugin, player);
        String next = strings[0];

        if (current.equalsIgnoreCase(next)) {
            player.sendMessage("Mutta hetkonen, sinähän olet " + next + "!");
            return true;
        }

        try {
            System.out.println("Saving profile " + current + " on " + player.getUniqueId().toString());
            savePlayer(player, current);
            loadPlayer(player, next);
            plugin.getServer().broadcastMessage("Tunnuksella " + player.getName() + " pelaa nyt " + next + "!");
            ActiveProfileManager.getInstance().setActiveProfile(plugin, player, next);
            System.out.println("Profile " + next + " is now active on " + player.getUniqueId().toString());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void savePlayer(Player player, String name) throws IOException {
        String uuid = player.getUniqueId().toString();
        File file = new File(plugin.getDataFolder().getAbsolutePath(),uuid + "." + name.toLowerCase() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        PlayerInventory inventory = player.getInventory();
        Location location = player.getLocation();
        Location bed = player.getBedSpawnLocation();

        // Items
        config.set("armor", inventory.getArmorContents());
        config.set("items", inventory.getContents());

        // Properties
        config.set("level", player.getLevel());
        config.set("xp", player.getExp());
        config.set("hp", player.getHealth());
        config.set("hunger", player.getFoodLevel());
        config.set("saturation", player.getSaturation());

        // Coordinates
        config.set("x", location.getX());
        config.set("y", location.getY());
        config.set("z", location.getZ());
        config.set("world", location.getWorld().getName());

        if (bed != null) {
            config.set("spawnX", bed.getX());
            config.set("spawnY", bed.getY());
            config.set("spawnZ", bed.getZ());
            config.set("spawnWorld", bed.getWorld().getName());
        }
        config.save(file);
    }

    private void loadPlayer(Player player, String name) {
        PlayerInventory inventory = player.getInventory();
        Server server = plugin.getServer();
        if (player.getName().equalsIgnoreCase(name)) {
            player.setDisplayName(player.getName());
        } else {
            player.setDisplayName(player.getName() + " (" + name + ")");
        }

        String uuid = player.getUniqueId().toString();
        File file = new File(plugin.getDataFolder().getAbsolutePath(),uuid + "." + name.toLowerCase() + ".yml");
        if (!file.exists()) {
            inventory.clear();
            player.setLevel(0);
            player.setExp(0);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(0);
            player.teleport(server.getWorlds().get(0).getSpawnLocation());
            player.setBedSpawnLocation(server.getWorlds().get(0).getSpawnLocation(), true);
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ItemStack[] armor = ((List<ItemStack>) config.get("armor")).toArray(new ItemStack[0]);
        inventory.setArmorContents(armor);

        ItemStack[] items = ((List<ItemStack>) config.get("items")).toArray(new ItemStack[0]);
        inventory.setContents(items);

        int level = config.getInt("level");
        player.setLevel(level);
        float exp = (float) config.getDouble("xp");
        player.setExp(exp);
        double hp = (float) config.getDouble("hp");
        player.setHealth(hp);
        int hunger = config.getInt("hunger");
        player.setFoodLevel(hunger);
        float saturation = (float) config.getDouble("saturation");
        player.setSaturation(saturation);

        double x = (float) config.getDouble("x");
        double y = (float) config.getDouble("y");
        double z = (float) config.getDouble("z");
        String world = (String) config.get("world");
        player.teleport(new Location(server.getWorld(world), x, y, z));

        if (config.isSet("spawnX")) {
            double spawnX = (float) config.getDouble("spawnX");
            double spawnY = (float) config.getDouble("spawnY");
            double spawnZ = (float) config.getDouble("spawnZ");
            String spawnWorld = (String) config.get("spawnWorld");
            player.setBedSpawnLocation(new Location(server.getWorld(spawnWorld), spawnX, spawnY, spawnZ), true);
        } else {
            // If this player has no bed, set to spawn
            System.out.println("Player has no bed, setting spawn location to spawn");
            player.setBedSpawnLocation(server.getWorlds().get(0).getSpawnLocation(), true);
        }
    }
}
