package com.floweytf.customitemapi.datadriven;

import com.floweytf.customitemapi.api.CustomItemRegistry;
import com.floweytf.customitemapi.api.DataLoaderRegistry;
import com.floweytf.customitemapi.datadriven.json.JsonCustomItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {
    public static final Logger LOGGER = LogManager.getLogger("CustomItemAPI/DataDriven");

    @Override
    public void onLoad() {
        final var registry = CustomItemRegistry.getInstance();
        DataLoaderRegistry.getInstance().addDatapackLoader(
            "items", manager -> manager.resources().forEach((id, resource) -> {
                try {
                    JsonCustomItem.readFromJson(registry, resource, id);
                } catch (Exception e) {
                    getSLF4JLogger().warn("Failed to load item {}", id, e);
                }
            })
        );
    }
}