package me.deqes.module.impl.render;

import com.google.common.eventbus.Subscribe;

import me.deqes.event.EventTick;
import me.deqes.module.Category;
import me.deqes.module.Module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public FullBright() {
        super("FullBright", Category.RENDER, 0);
    }

    @Subscribe
    public void onTick(EventTick e) {

        if (mc.player == null)
            return;

        mc.player.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        220,
                        0,
                        false,
                        false
                )
        );
    }

    @Override
    public void onDisable() {

        if (mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }

        super.onDisable();
    }
}