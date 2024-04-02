package com.killshot.config;

import com.killshot.Killshot;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class KillshotConfigModel {
    private final ForgeConfigSpec.BooleanValue isEnabled;
    private final ForgeConfigSpec.BooleanValue respawnImmediately;

    public static final String CONFIG_FILE_NAME = Killshot.MODID + ".toml";

    private KillshotConfigModel(final ForgeConfigSpec.Builder builder) {
        this.isEnabled = builder.define("isEnabled", isEnabledDefault());
        this.respawnImmediately = builder.define("respawnImmediately", respawnImmediatelyDefault());
    }

    private static boolean isEnabledDefault() {
        return true;
    }

    private static boolean respawnImmediatelyDefault() {
        return false;
    }

    public static Pair<KillshotConfigModel, ForgeConfigSpec> init() {
        return new ForgeConfigSpec.Builder().configure(KillshotConfigModel::new);
    }

    public boolean isEnabled() {
        return this.isEnabled.get();
    }

    public void isEnabled(final boolean isEnabled) {
        this.isEnabled.set(isEnabled);
    }

    public boolean respawnImmediately() {
        return this.respawnImmediately.get();
    }

    public void respawnImmediately(final Boolean respawnImmediately) {
        this.respawnImmediately.set(respawnImmediately);
    }

    public void setToDefault() {
        isEnabled.set(isEnabledDefault());
        respawnImmediately.set(isEnabledDefault());
    }
}