package io.github.lapis256.torcherino_compat.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.lapis256.torcherino_compat.utils.MekanismUtils;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
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
    @Final
    private final Set<MultiblockData> torcherinoCompat$acceleratedMultiblocks = new HashSet<>();

    public MixinTorcherinoBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private static void torcherinoCompat$tick(CallbackInfo ci, @Local(argsOnly = true) TorcherinoBlockEntity entity) {
        ((MixinTorcherinoBlockEntity) (Object) entity).torcherinoCompat$acceleratedMultiblocks.clear();
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
    private void torcherinoCompat$tickBlock(
            BlockPos pos,
            CallbackInfo ci,
            @Local(ordinal = 0) BlockState blockState,
            @Local(ordinal = 0) BlockEntity blockEntity,
            @Local(ordinal = 0) BlockEntityTicker<BlockEntity> ticker
    ) {
        if (!(blockEntity instanceof TileEntityMultiblock<?> multi)) {
            return;
        }

        var data = multi.getMultiblock();
        if (torcherinoCompat$acceleratedMultiblocks.contains(data) || MekanismUtils.isCancelAcceleration(data)) {
            ci.cancel();
            return;
        }
        torcherinoCompat$acceleratedMultiblocks.add(data);

        if (!data.isFormed() || MekanismUtils.isCancelAcceleration(data)) {
            ci.cancel();
            return;
        }

        var isMaster = multi.isMaster();
        var multiTile = (AccessorTileEntityMultiblock) multi;

        multiTile.setIsMaster(true);
        for (int i = 0; i < this.speed; i++) {
            if (blockEntity.isRemoved() || MekanismUtils.isCancelAcceleration(data)) {
                break;
            }
            ticker.tick(Objects.requireNonNull(this.level), pos, blockState, blockEntity);
        }
        multiTile.setIsMaster(isMaster);
        ci.cancel();
    }
}
