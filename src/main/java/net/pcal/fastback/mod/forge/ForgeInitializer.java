package net.pcal.fastback.mod.forge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

/**
 * @author pcal
 * @since 0.16.0
 */
@Mod(modid = "fastback", version = Tags.VERSION, name = "Fast Backups", acceptedMinecraftVersions = "[1.7.10]", acceptableRemoteVersions = "*")
final public class ForgeInitializer {

    @SidedProxy(clientSide = "net.pcal.fastback.mod.forge.ForgeClientProvider", serverSide = "net.pcal.fastback.mod.forge.ForgeCommonProvider")
    static ForgeCommonProvider proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.onPreInitializationEvent(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.onInitializationEvent(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
       proxy.onServerStartupEvent(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        proxy.onServerStoppingEvent(event);
    }
}
