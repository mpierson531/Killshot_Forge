package com.killshot.config;

import com.killshot.Killshot;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KillshotConfigScreen extends Screen {
    protected Screen parent;

    protected Button enabledButton;
    protected Button respawnImmediatelyButton;
    protected Button killButton;
    protected Button respawnButton;

    protected Button doneButton;

    private static final int VERTICAL_SPACING = 5;
    private static final int HORIZONTAL_SPACING = 5;
    private static final int BUTTON_WIDTH = 150;
    private static final int HALF_BUTTON_WIDTH = BUTTON_WIDTH / 2;
    private static final int HALF_BUTTON_WIDTH_W_SPACING = HALF_BUTTON_WIDTH - HORIZONTAL_SPACING;
    private static final int BUTTON_HEIGHT = 20;

    private int getEnabledY() {
        return (super.height / 2) - (BUTTON_HEIGHT / 2);
    }

    private static String enabledToString(final boolean enabled) {
        return enabled ? "Disable" : "Enable";
    }
    
    private static Component literal(final String message) {
        return Component.literal(message);
    }
    
    private static Tooltip newTooltip(final String message) {
        return Tooltip.create(literal(message));
    }

    private static Tooltip getEnabledTooltip() {
        return newTooltip(enabledToString(Killshot.getInstance().config.isEnabled()) + " Killshot");
    }

    private static Component getEnabledButtonText() {
        return literal("Enabled: " + String.valueOf(Killshot.getInstance().config.isEnabled()));
    }

    private int getRespawnImmediatelyY() {
        return enabledButton.getY() + BUTTON_HEIGHT + VERTICAL_SPACING;
    }

    private static Tooltip getRespawnImmediatelyTooltip() {
        return newTooltip(enabledToString(Killshot.getInstance().config.respawnImmediately()) + " respawning immediately when Killshot is activated");
    }

    private static Component getRespawnImmediatelyButtonText() {
        return literal("Respawn immediately: " + String.valueOf(Killshot.getInstance().config.respawnImmediately()));
    }

    private int getKillY() {
        return respawnImmediatelyButton.getY() + BUTTON_HEIGHT + VERTICAL_SPACING;
    }

    private static Component getKillText() {
        return literal("Kill");
    }

    private static Tooltip getKillTooltip() {
        return newTooltip("Kill yourself");
    }

    private int getRespawnX() {
        return killButton.getX() + HALF_BUTTON_WIDTH + HORIZONTAL_SPACING;
    }

    private int getRespawnY() {
        return killButton.getY();
    }

    private static Component getRespawnText() {
        return literal("Respawn");
    }

    private static Tooltip getRespawnTooltip() {
        return newTooltip("Kill yourself and respawn");
    }

    private int getDoneY() {
        return super.height - 28;
    }

    public KillshotConfigScreen(Screen parent) {
        super(literal("Killshot Config"));

        this.parent = parent;
    }

    private void isEnabledOnClick() {
        final KillshotConfigModel config = Killshot.getInstance().config;
        config.isEnabled(!config.isEnabled());

        enabledButton.setMessage(getEnabledButtonText());
        enabledButton.setTooltip(getEnabledTooltip());
    }

    private void respawnImmediatelyOnClick() {
        final KillshotConfigModel config = Killshot.getInstance().config;
        config.respawnImmediately(!config.respawnImmediately());

        respawnImmediatelyButton.setMessage(getRespawnImmediatelyButtonText());
        respawnImmediatelyButton.setTooltip(getRespawnImmediatelyTooltip());
    }

    @Override
    protected void init() {
        super.init();

        final int x = (super.width / 2) - (BUTTON_WIDTH / 2);

        final Component enabledButtonText = getEnabledButtonText();
        final Component respawnImmediatelyButtonText = getRespawnImmediatelyButtonText();
        final Component killText = getKillText();
        final Component respawnText = getRespawnText();

        final int doneY = getDoneY();

        enabledButton = PlainTextButton.builder(enabledButtonText, button -> isEnabledOnClick())
                .bounds(x, getEnabledY(), BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(getEnabledTooltip())
                .build();

        respawnImmediatelyButton = PlainTextButton.builder(respawnImmediatelyButtonText, button -> respawnImmediatelyOnClick())
                .bounds(x, getRespawnImmediatelyY(), BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(getRespawnImmediatelyTooltip())
                .build();

        if (Killshot.getInstance().isInWorld()) {
            killButton = Button.builder(killText, button -> Killshot.getInstance().kill(Killshot.getServer()))
                    .bounds(x, getKillY(), HALF_BUTTON_WIDTH, BUTTON_HEIGHT)
                    .tooltip(getKillTooltip())
                    .build();

            respawnButton = Button.builder(respawnText, button -> Killshot.getInstance().respawn(Killshot.getServer()))
                    .bounds(getRespawnX(), getRespawnY(), HALF_BUTTON_WIDTH_W_SPACING, BUTTON_HEIGHT)
                    .tooltip(getRespawnTooltip())
                    .build();
        }

        doneButton = Button.builder(literal("Done"), button -> onClose())
                .bounds(x, doneY, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        super.addRenderableWidget(enabledButton);
        super.addRenderableWidget(respawnImmediatelyButton);

        if (Killshot.getInstance().isInWorld()) {
            super.addRenderableWidget(killButton);
            super.addRenderableWidget(respawnButton);
        }

        super.addRenderableWidget(doneButton);
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}
