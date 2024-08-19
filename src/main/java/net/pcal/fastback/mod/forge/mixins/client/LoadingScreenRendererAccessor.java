package net.pcal.fastback.mod.forge.mixins.client;

import net.minecraft.client.LoadingScreenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoadingScreenRenderer.class)
public interface LoadingScreenRendererAccessor {
    @Accessor
    void setCurrentlyDisplayedText(String value);
}
