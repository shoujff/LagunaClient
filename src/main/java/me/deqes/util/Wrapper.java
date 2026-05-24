package me.deqes.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public interface Wrapper {
    MinecraftClient mc = MinecraftClient.getInstance();
    TextRenderer textRenderer = mc.textRenderer;
}
