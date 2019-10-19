package hamberg;

import org.bukkit.plugin.java.JavaPlugin;

public class MultiUserTeleport extends JavaPlugin {
    @Override
    public void onEnable() {
        System.out.println(MultiUserTeleport.class.getSimpleName() +  " ready to rock!");
        this.getCommand("vaihda").setExecutor(new CommandVaihda(this));
        this.getCommand("kuka").setExecutor(new CommandKuka(this));
        this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
    }
}
