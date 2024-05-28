package io.github.lapis256.torcherino_compat;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Mod(TorcherinoCompat.MOD_ID)
public class TorcherinoCompat {
    public static final String MOD_ID = "torcherino_compat";
    public static final String MOD_NAME = "Torcherino Compat";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public TorcherinoCompat() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.INSTANCE.spec);
    }
}
