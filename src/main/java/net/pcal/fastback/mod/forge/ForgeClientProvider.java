package net.pcal.fastback.mod.forge;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.pcal.fastback.logging.UserMessage;
import net.pcal.fastback.mod.forge.mixins.client.LoadingScreenRendererAccessor;

import static java.util.Objects.requireNonNull;
import static net.pcal.fastback.logging.SystemLogger.syslog;
import static net.pcal.fastback.mod.MinecraftProvider.messageToText;

/**
 * Handles client-specific tasks.
 *
 * @author pcal
 * @since 0.16.0
 */
final class ForgeClientProvider extends ForgeCommonProvider {

    // ======================================================================
    // Constants

    private static final long TEXT_TIMEOUT = 10 * 1000;

    // ======================================================================
    // Fields

    //private MinecraftClient client = null;
    private String hudText;
    private long hudTextTime;
    private Minecraft client;

    void onPreInitializationEvent(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventBusHandler());
        this.client = requireNonNull(Minecraft.getMinecraft(), "Minecraft.getMinecraft() returned null");
    }

    // ======================================================================
    // Forge Event handlers

    private final class ClientEventBusHandler {
        @SubscribeEvent
        private void onDrawScreenEvent(GuiScreenEvent.DrawScreenEvent.Post event) {
            if (hudText == null) return;
            // if (!this.client.options.getShowAutosaveIndicator().getValue()) return; FIXME
            if (System.currentTimeMillis() - hudTextTime > TEXT_TIMEOUT) {
                // Don't leave it sitting up there forever if we fail to call clearHudText()
                hudText = null;
                syslog().debug("hud text timed out.  somebody forgot to clean up");
                return;
            }
            if (client != null) {
                event.gui.drawString(client.fontRenderer, hudText, 2, 2, 1);
            }
        }
    }

    // ======================================================================
    // MinecraftProvider implementation

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public void setHudText(UserMessage userMessage) {
        if (userMessage == null) {
            clearHudText();
        } else {
            this.hudText = messageToText(userMessage).getFormattedText(); // so the hud renderer can find it
            this.hudTextTime = System.currentTimeMillis();
        }
    }

    @Override
    public void clearHudText() {
        this.hudText = null;
        // TODO someday it might be nice to bring back the fading text effect.  But getting to it properly
        // clean up 100% of the time is more than I want to deal with right now.
    }

    @Override
    public void setMessageScreenText(UserMessage userMessage) {
        final IChatComponent text = messageToText(userMessage);
        this.hudText = text.getFormattedText();
        final LoadingScreenRenderer screen = client.loadingScreen;
        if (screen != null) ((LoadingScreenRendererAccessor) screen).setCurrentlyDisplayedText(text.getUnformattedTextForChat());
    }
}
