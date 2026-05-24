package me.deqes.mixin;

import me.deqes.Laguna;
import me.deqes.event.EventKey;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    public void onKey(long window, int action, KeyInput input, CallbackInfo ci)
    {
        if (Laguna.getInstance() != null && Laguna.getInstance().getEventBus() != null) {
            EventKey eventKey = new EventKey(input.key(), action);
            Laguna.getInstance().getEventBus().post(eventKey);
        }
    }

}
