package me.deqes.mixin;

import me.deqes.Laguna;
import me.deqes.command.Command;
import me.deqes.event.EventMessage;
import net.minecraft.client.gui.screen.ChatScreen;
import org.apache.logging.log4j.core.jackson.ContextDataAsEntryListSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {


    @Inject(method="sendMessage", at = @At("HEAD"), cancellable = true)
    public void onMessage(String chatText, boolean addToHistory, CallbackInfo ci){
        boolean isCmd;
        if(chatText.startsWith(Command.prefix))
        {
            isCmd = true;
            ci.cancel();

        } else{
            isCmd=false;
        }
        EventMessage eventMessage = new EventMessage(chatText, isCmd);
        Laguna.getInstance().getEventBus().post(eventMessage);

    }
}
