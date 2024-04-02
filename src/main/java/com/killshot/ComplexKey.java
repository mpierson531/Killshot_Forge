package com.killshot;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ComplexKey {
    private final KeyMapping simpleBinding;

    private ComplexKey(final KeyMapping simpleBinding) {
        this.simpleBinding = simpleBinding;
    }

    static String getDefaultBindingName() {
        return "Shoot the shot";
    }

    static InputConstants.Type getDefaultType() {
        return InputConstants.Type.KEYSYM;
    }

    static int getDefaultKeycode() {
        return GLFW.GLFW_KEY_COMMA;
    }

    private static KeyMapping getDefaultBaseBinding() {
        return new KeyMapping(
                getDefaultBindingName(),
                KeyConflictContext.IN_GAME,
                getDefaultType(),
                getDefaultKeycode(),
                "Killshot"
        );
    }

    static ComplexKey getDefaultBinding() {
        final KeyMapping defaultBase = getDefaultBaseBinding();
        return new ComplexKey(defaultBase);
    }

    ComplexKey register(RegisterKeyMappingsEvent event) {
        event.register(simpleBinding);
        return this;
    }

    ComplexKey deregister() throws KillshotException {
        throw new KillshotException("com.killshot.ComplexKey::deregister not implemented yet!", "");
    }

    private boolean simpleIsPressed() {
        return simpleBinding.isDown();
    }

    private boolean simpleWasPressed() {
        return simpleBinding.consumeClick();
    }

    boolean isPressed() {
        return simpleIsPressed();
    }

    boolean wasPressed() {
        return simpleWasPressed();
    }
}