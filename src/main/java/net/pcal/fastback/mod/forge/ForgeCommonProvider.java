package net.pcal.fastback.mod.forge;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.pcal.fastback.logging.SystemLogger;
import net.pcal.fastback.logging.UserMessage;
import net.pcal.fastback.mod.LifecycleListener;
import net.pcal.fastback.mod.MinecraftProvider;
import net.pcal.fastback.mod.forge.mixins.MinecraftServerAccessor;
import org.apache.logging.log4j.LogManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static net.pcal.fastback.commands.Commands.createBackupCommand;
import static net.pcal.fastback.logging.SystemLogger.syslog;
import static net.pcal.fastback.mod.MinecraftProvider.messageToText;

/**
 * @author pcal
 * @since 0.16.0
 */
class ForgeCommonProvider implements MinecraftProvider {

    static final String MOD_ID = "fastback";
    private MinecraftServer logicalServer;
    private LifecycleListener lifecycleListener = null;
    private Runnable autoSaveListener;
    private boolean isWorldSaveEnabled;

    void onPreInitializationEvent(FMLPreInitializationEvent event) {
    }

    // ======================================================================
    // Forge Event handlers

    void onInitializationEvent(FMLInitializationEvent event) {
        this.onInitialize();
    }

    void onServerStartingEvent(FMLServerStartingEvent event) {
        event.registerServerCommand(createBackupCommand());
    }

    void onServerStartupEvent(FMLServerStartedEvent event) {
        this.logicalServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        requireNonNull(this.lifecycleListener).onWorldStart();
    }

    void onServerStoppingEvent(FMLServerStoppingEvent event) {
        requireNonNull(this.lifecycleListener).onWorldStop();
        this.logicalServer = null;
    }

    /**
     TODO This one isn't it.  We need to hear about it when an autosaves (and only autosaves) are completed.
     Might have to delve into Forge mixins to do this.
     private void onLevelSaveEvent(LevelEvent.Save event) {
     provider.onAutoSaveComplete();
     }
     **/


    // ======================================================================
    // Protected

    /**
     * This is the key initialization routine.  Registers the logger, the frameworkprovider and the commands
     * where the rest of the mod can get at them.
     */
    void onInitialize() {
        SystemLogger.Singleton.register(new Slf4jSystemLogger(LogManager.getLogger(MOD_ID)));
        this.lifecycleListener = MinecraftProvider.register(this);
        syslog().debug("registered backup command");
        this.lifecycleListener.onInitialize();
        SshHacks.ensureSshSessionFactoryIsAvailable();
        syslog().info("Fastback initialized");
        syslog().warn("------------------------------------------------------------------------------------");
        syslog().warn("Thanks for trying the new Forge version of Fastback.  For help, go to:");
        syslog().warn("https://pcal43.github.io/fastback/");
        syslog().warn("Please note that this is an alpha release.  A list of known issues is available here:");
        syslog().warn("https://github.com/pcal43/fastback/issues?q=is%3Aissue+is%3Aopen+label%3Aforge");
        syslog().warn("------------------------------------------------------------------------------------");
    }


    // ======================================================================
    // Fastback MinecraftProvider implementation

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public void setHudText(UserMessage userMessage) {
    }

    @Override
    public void clearHudText() {
    }

    @Override
    public void setMessageScreenText(UserMessage userMessage) {
    }

    @Override
    public String getModVersion() {
        return "0.15.3+1.20.1-alpha"; //FIXME
    }

    //FIXME!!
    void onAutoSaveComplete() {
        syslog().debug("onAutoSaveComplete");
        this.autoSaveListener.run();
    }

    @Override
    public Path getWorldDirectory() {
        if (DimensionManager.getCurrentSaveRootDirectory() == null) throw new IllegalStateException("DimensionManager#getCurrentSaveRootDirectory is null");
        return DimensionManager.getCurrentSaveRootDirectory().toPath().toAbsolutePath().normalize();
    }

    @Override
    public void setWorldSaveEnabled(boolean enabled) {
        for (WorldServer world : logicalServer.worldServers) {
            world.levelSaving = enabled;
        }
    }

    @Override
    public void saveWorld() {
        if (this.logicalServer == null) throw new IllegalStateException();
        ((MinecraftServerAccessor) this.logicalServer).callSaveAllWorlds(false);
    }

    @Override
    public void sendBroadcast(UserMessage userMessage) {
        if (this.logicalServer != null && this.logicalServer.isDedicatedServer()) {
            logicalServer.getConfigurationManager().sendChatMsg(messageToText(userMessage));
        }
    }

    @Override
    public void setAutoSaveListener(Runnable runnable) {
        this.autoSaveListener = requireNonNull(runnable);
    }

    @Override
    public Path getSavesDir() {
        if (this.isClient()) {
            return FMLClientHandler.instance().getSavesDir().toPath().toAbsolutePath().normalize();
        } else {
            return null;
        }
    }

    @Override
    public String getWorldName() {
        return this.logicalServer.getFolderName();
    }

    /**
     * Add extra properties that will be stored in .fastback/backup.properties.
     */
    @Override
    public void addBackupProperties(Map<String, String> props) {
        props.put("fastback-version", this.getModVersion());
        if (this.logicalServer != null) {
            props.put("minecraft-version", logicalServer.getMinecraftVersion());
            props.put("minecraft-game-mode", logicalServer.getGameType().getName());
            props.put("minecraft-level-name", logicalServer.getFolderName());
        }
    }

    /**
     * @return paths to the files and directories that should be backed up when config-backup is enabled.
     */
    @Override
    public Collection<Path> getModsBackupPaths() {
        final List<Path> out = new ArrayList<>();
        /**
         final FabricLoader fl = FabricLoader.getInstance();
         final Path gameDir = fl.getGameDir();
         out.add(gameDir.resolve("options.txt´"));
         out.add(gameDir.resolve("mods"));
         out.add(gameDir.resolve("config"));
         out.add(gameDir.resolve("resourcepacks"));
         **/
        return out;
    }

}
