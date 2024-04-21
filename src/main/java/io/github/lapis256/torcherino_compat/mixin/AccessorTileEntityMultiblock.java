package io.github.lapis256.torcherino_compat.mixin;

import mekanism.common.tile.prefab.TileEntityMultiblock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TileEntityMultiblock.class, remap = false)
public interface AccessorTileEntityMultiblock {
    @Accessor("isMaster")
    void setIsMaster(boolean isMaster);
}
