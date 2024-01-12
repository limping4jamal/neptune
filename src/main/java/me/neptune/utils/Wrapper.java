package me.neptune.utils;

import me.neptune.interfaces.IMinecraftClient;
import net.minecraft.client.MinecraftClient;

public interface Wrapper {
    MinecraftClient mc = MinecraftClient.getInstance();
    IMinecraftClient imc = (IMinecraftClient) mc;
}
