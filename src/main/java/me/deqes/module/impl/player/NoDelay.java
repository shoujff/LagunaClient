package me.deqes.module.impl.player;

import com.google.common.eventbus.Subscribe;

import me.deqes.event.EventTick;
import me.deqes.module.Category;
import me.deqes.module.Module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

import java.lang.reflect.Field;

public class NoDelay extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    private Field itemUseCooldownField;
    private Field jumpingCooldownField;
    private Field attackCooldownField;

    public NoDelay() {

        super("NoDelay", Category.PLAYER, 0);

        try {

            itemUseCooldownField =
                    MinecraftClient.class.getDeclaredField("itemUseCooldown");

            itemUseCooldownField.setAccessible(true);

        } catch (Exception ignored) {
        }

        try {

            jumpingCooldownField =
                    LivingEntity.class.getDeclaredField("jumpingCooldown");

            jumpingCooldownField.setAccessible(true);

        } catch (Exception ignored) {
        }

        try {

            attackCooldownField =
                    MinecraftClient.class.getDeclaredField("attackCooldown");

            attackCooldownField.setAccessible(true);

        } catch (Exception ignored) {
        }
    }

    @Subscribe
    public void onTick(EventTick e) {

        if (mc.player == null)
            return;

        try {

            if (itemUseCooldownField != null)
                itemUseCooldownField.setInt(mc, 0);

        } catch (Exception ignored) {
        }

        try {

            if (jumpingCooldownField != null)
                jumpingCooldownField.setInt(mc.player, 0);

        } catch (Exception ignored) {
        }

        try {

            if (attackCooldownField != null)
                attackCooldownField.setInt(mc, 0);

        } catch (Exception ignored) {
        }
    }
}