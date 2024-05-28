package io.github.lapis256.torcherino_compat.utils;

import io.github.lapis256.torcherino_compat.Config;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;

public class MekanismUtils {
    public static boolean isCancelAcceleration(MultiblockData data) {
        return Config.MEKANISM_FISSION_REACTOR_DAMAGE_PROTECTION.get() &&
                data instanceof FissionReactorMultiblockData fission &&
                fission.isBurning() &&
                (fission.reactorDamage > Config.MEKANISM_FISSION_REACTOR_DAMAGE_THRESHOLD.get());
    }
}
