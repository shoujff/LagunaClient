package me.deqes.mixin;
import lombok.AllArgsConstructor;
import me.deqes.Laguna;
import me.deqes.event.EventMouse;
import me.deqes.event.EventTick;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.deqes.util.Wrapper.mc;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseButton", at=@At("HEAD"))
    public void onMouse(long window, MouseInput input, int action, CallbackInfo ci){
        double mouseX = mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth();
        double mouseY = mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight();
        EventMouse eventMouse = new EventMouse(input.button(), action, mouseX, mouseY);
        Laguna.getInstance().getEventBus().post(eventMouse);


    }

}
