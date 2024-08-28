package com.fullskele.flaskoflife;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ConfigHandler {

    public static Configuration config;

    public static String[] REFILL_BLOCKS;
    public static float REFILL_COOLDOWN_SCALING;
    public static boolean TOOLTIP_NEEDS_SHIFT;

    public static float USE_VOLUME;
    public static float USE_PITCH;
    public static float REFILL_VOLUME;
    public static float REFILL_PITCH;



    public static void init(File file) {
        config = new Configuration(file);
        String category;

        category = "Flask General Settings";

        REFILL_BLOCKS = config.getStringList("Refill Blocks", category, new String[]{"minecraft:beacon", "minecraft:bed"}, "Blocks that can refill eligible flasks with a shift-right click");
        REFILL_COOLDOWN_SCALING = config.getFloat("Refill Cooldown Scaling", category, 1.0f, 0.0f, 10000.0f, "The amount to multiply flask cooldowns by when refilling. 0.0 to disable refill cooldowns.");
        TOOLTIP_NEEDS_SHIFT = config.getBoolean("Hold Shift For Stats", category, false, "Should shift need to be held to view flask stats tooltip? 'false' to always display stats on hover.");

        category = "Flask Sound Settings";
        USE_VOLUME = config.getFloat("Heal Sound: Volume", category, 1.0f, 0.0f, 1.0f, "");
        USE_PITCH = config.getFloat("Heal Sound: Pitch", category, 0.0f, -10.0f, 10.0f, "");


        REFILL_VOLUME = config.getFloat("Refill Sound: Volume", category, 1.0f, 0.0f, 1.0f, "");
        REFILL_PITCH = config.getFloat("Refill Sound: Pitch", category, 0.0f, -10.0f, 10.0f, "");

        config.save();
    }

    public static void RegisterConfig(FMLPreInitializationEvent event) {
        FlaskOfLife.config = new File(event.getModConfigurationDirectory() + "/");
        init(new File(FlaskOfLife.config.getPath(), FlaskOfLife.MODID + ".cfg"));
    }
}
