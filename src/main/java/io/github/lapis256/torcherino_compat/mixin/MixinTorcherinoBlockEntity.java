package io.github.lapis256.torcherino_compat.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Mixin(value = TorcherinoBlockEntity.class, remap = false)
public abstract class MixinTorcherinoBlockEntity extends BlockEntity {
    @Shadow
    private int speed;

    @Unique
    private final Set<MultiblockData> torcherinoIntegrations$acceleratedMultiblocks = new HashSet<>();

    public MixinTorcherinoBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private static void tick(Level level, BlockPos pos, BlockState state, TorcherinoBlockEntity entity, CallbackInfo ci) {
        ((MixinTorcherinoBlockEntity) (Object) entity).torcherinoIntegrations$acceleratedMultiblocks.clear();
    }

    @Inject(
            method = "tickBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BlockEntity;isRemoved()Z",
                    shift = At.Shift.BEFORE,
                    ordinal = 1
            ),
            cancellable = true,
            remap = true
    )
    private void tickBlock(
            BlockPos pos,
            CallbackInfo ci,
            @Local(ordinal = 0) BlockState blockState,
            @Local(ordinal = 0) BlockEntity blockEntity,
            @Local(ordinal = 0) BlockEntityTicker<BlockEntity> ticker
    ) {
        if (!(blockEntity instanceof TileEntityMultiblock<?> multi)) {
            return;
        }
        var data = multi.getDefaultData();
        if (data != null && torcherinoIntegrations$acceleratedMultiblocks.contains(data)) {
            return;
        }
        torcherinoIntegrations$acceleratedMultiblocks.add(data);

        var isMaster = multi.isMaster();
        var multiTile = (AccessorTileEntityMultiblock) multi;

        multiTile.setIsMaster(true);
        for (int i = 0; i < this.speed; i++) {
            if (blockEntity.isRemoved()) {
                break;
            }
            ticker.tick(Objects.requireNonNull(this.level), pos, blockState, blockEntity);
        }
        multiTile.setIsMaster(isMaster);
        ci.cancel();
    }
}
