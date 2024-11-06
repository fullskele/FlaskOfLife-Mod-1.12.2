package com.fullskele.flaskoflife.proxy;

import com.fullskele.flaskoflife.FlaskOfLife;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;


@Mod.EventBusSubscriber(modid = FlaskOfLife.MODID, value = CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public static void registerTextures(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(FlaskOfLife.Items.flask_healing, 0, new ModelResourceLocation(FlaskOfLife.Items.flask_healing.getRegistryName(), "inventory"));
    }

}
