package io.github.lapis256.torcherino_compat;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    static final Config INSTANCE = new Config();
    final ForgeConfigSpec spec;

    public static ForgeConfigSpec.BooleanValue MEKANISM_FISSION_REACTOR_DAMAGE_PROTECTION;
    public static ForgeConfigSpec.IntValue MEKANISM_FISSION_REACTOR_DAMAGE_THRESHOLD;

    Config() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Torcherino Compat Common Config.");

        builder.push("mekanism");
        builder.comment("Fission Reactor Compatibility Config.");
        builder.push("fission_reactor");
        MEKANISM_FISSION_REACTOR_DAMAGE_PROTECTION = builder.comment("Temporarily pause acceleration if a burning fission reactor is damaged.").define("damageProtection", true);
        MEKANISM_FISSION_REACTOR_DAMAGE_THRESHOLD = builder.comment("Threshold for fission reactor damage.").defineInRange("damageThreshold", 0, 0, 100);
        builder.pop(2);

        spec = builder.build();
    }
}
