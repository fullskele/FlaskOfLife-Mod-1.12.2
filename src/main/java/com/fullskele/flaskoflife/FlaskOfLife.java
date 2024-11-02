package com.fullskele.flaskoflife;

import com.fullskele.flaskoflife.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.HashSet;

@Mod(modid = FlaskOfLife.MODID, name = FlaskOfLife.NAME, version = FlaskOfLife.VERSION)
public class FlaskOfLife {
    public static final String MODID = "flaskoflife";
    public static final String NAME = "Flask of Life";
    public static final String VERSION = "1.0.2";

    public static File config;
    public static HashSet<Block> refillBlocks = new HashSet<>();

    @Mod.Instance
    public static FlaskOfLife instance;

    @SidedProxy(clientSide = "com.fullskele.flaskoflife.proxy.ClientProxy", serverSide = "com.fullskele.flaskoflife.proxy.CommonProxy")
    public static CommonProxy commonProxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.RegisterConfig(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        for (String blockString : ConfigHandler.REFILL_BLOCKS) {
            Block block = Block.getBlockFromName(blockString);
            if (block != null) {
                refillBlocks.add(block);
            }
        }
    }


    @GameRegistry.ObjectHolder(FlaskOfLife.MODID)
    public static class Items {
        public static final ItemFlask flask_healing = new ItemFlask("flask_healing");

    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {

            event.getRegistry().register(Items.flask_healing);
        }
    }

}
