package me.deqes.module.impl.movement;

import com.google.common.eventbus.Subscribe;
import me.deqes.event.EventTick;           // ← Изменено
import me.deqes.module.Category;
import me.deqes.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoSprint extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private int resetTimer = 0;

    public AutoSprint() {
        super("AutoSprint", Category.MOVEMENT, 0);
    }

    @Subscribe
    public void onTick(EventTick e) {          // ← Изменено на EventTick
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        // Автоспринт
        if (isMovingForward() && !player.isSneaking() && !player.isSwimming()
                && player.getHungerManager().getFoodLevel() > 6) {

            player.setSprinting(true);
        }

        // Сброс спринта
        if (resetTimer > 0) {
            resetTimer--;
            if (resetTimer == 0) {
                player.setSprinting(true);
            }
        }
    }

    /**
     * Вызывай этот метод перед атакой в Killaura
     */
    public void resetSprint() {
        if (mc.player != null) {
            mc.player.setSprinting(false);
            resetTimer = 2; // 2 тика — хороший баланс легитности
        }
    }

    private boolean isMovingForward() {
        return mc.options.forwardKey.isPressed()
                && !mc.options.backKey.isPressed();
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.setSprinting(false);
        }
        resetTimer = 0;
        super.onDisable();
    }
}