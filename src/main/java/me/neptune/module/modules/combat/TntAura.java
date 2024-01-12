package me.neptune.module.modules.combat;

import com.google.common.collect.Lists;
import me.neptune.cmd.CommandManager;
import me.neptune.gui.Color;
import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.EnumSetting;
import me.neptune.settings.SliderSetting;
import me.neptune.utils.*;
import net.minecraft.block.TntBlock;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TntAura extends Module {

    private final SliderSetting range = new SliderSetting("Find Target Range", "range_target", 6.0f, 0.0f, 6.0f, 0.1);
    private final SliderSetting placeDelay = new SliderSetting("Delay/Place", "delay", 3, 0, 20, 1);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "rotate",false);
    private final EnumSetting renderMode = new EnumSetting("Render Mode", RenderMode.Fade);

    public TntAura() {
        super("TNT Aura", Category.Combat);
        this.setDescription("Blows up TNT onto the Enemy's Head");
        this.addSetting(range);
        this.addSetting(placeDelay);
        this.addSetting(rotate);
        this.addSetting(renderMode);
    }

    private final Map<BlockPos, Long> renderPoses = new ConcurrentHashMap<>();
    public static Timer inactivityTimer = new Timer();
    private static PlayerEntity target;

    private int delay;

    @Override
    public void onEnable() {
        super.onEnable();
        target = null;
        renderPoses.clear();
        inactivityTimer.reset();
    }

    @Override
    public void onRender(MatrixStack matrixStack, float partialTicks) {
        if (!renderPoses.isEmpty()) {
            renderPoses.forEach((pos, time) -> {
                if (System.currentTimeMillis() - time > 500) {
                    renderPoses.remove(pos);
                } else {
                    if (renderMode.getValue().equals(RenderMode.Fade)) {
                        this.getRenderUtils().draw3DBox(matrixStack, new Box(pos), new Color(255, 255, 255), 0.2f);
                    } else if (renderMode.getValue().equals(RenderMode.Decrease)) {
                        this.getRenderUtils().draw3DBox(matrixStack, new Box(pos), new Color(255, 255, 255), 0.2f);
                    }
                }
            });
        }
    }

    @Override
    public void onUpdate() {
        delay++;
        if (getTntSlot() == -1) {
            CommandManager.sendChatMessage(Formatting.RED + "No Tnt!");
            disable();
            return;
        }
        if (getFlintSlot() == -1) {
            CommandManager.sendChatMessage(Formatting.RED + "No Flint and Steel!");
            disable();
            return;
        }

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != mc.player) {
                if (mc.player.distanceTo(player) > range.getValueInt()) target = null; else target = player;
            }
        }

        if (target == null) {
            CommandManager.sendChatMessage(Formatting.RED + "No Target!");
            disable();
            return;
        }

        BlockPos headBlock = BlockPos.ofFloored(target.getPos()).up(2);

        if (delay >= placeDelay.getValueInt()) {
            InventoryUtil.doSwap(getTntSlot());
            if (mc.world.getBlockState(headBlock).isAir()) BlockUtil.placeBlock(headBlock, rotate.getValue());
            InventoryUtil.doSwap(getFlintSlot());
            renderPoses.put(headBlock, System.currentTimeMillis());
            delay = 0;
        }

        if (mc.world.getBlockState(headBlock).getBlock() instanceof TntBlock) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(headBlock.getX(), headBlock.getY(), headBlock.getZ()), Direction.DOWN, headBlock, true));
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private List<BlockPos> getBlocks(PlayerEntity pl) {
        if (pl == null) return new ArrayList<>();
        List<BlockPos> blocks = new ArrayList<>();
        for (BlockPos bp : getAffectedBlocks(pl)) {
            for (Direction dir : Direction.values()) {
                if (dir == Direction.UP || dir == Direction.DOWN) continue;
                blocks.add(bp.offset(dir));
                blocks.add(bp.offset(dir).up());

                if (!new Box(bp.offset(dir).up(1)).intersects(pl.getBoundingBox()))
                    blocks.add(bp.offset(dir).up(2));

                blocks.add(bp.offset(dir).down());
            }

            blocks.add(bp.down());
            blocks.add(bp.up(3));

            if (!BlockUtil.canPlace(bp.up(3), 6)) {
                Direction dir = mc.player.getHorizontalFacing();
                if (dir != null) {
                    blocks.add(bp.up(3).offset(dir, 1));
                }
            }
        }

        return blocks.stream().sorted(Comparator.comparing(b -> mc.player.squaredDistanceTo(b.toCenterPos()) * -1)).toList();
    }

    private int getTntSlot() {
        if (mc.player.getMainHandStack().getItem() == Items.TNT)
            return mc.player.getInventory().selectedSlot;

        int slot = -1;

        slot = InventoryUtil.findItemInventorySlot(Items.TNT, false);
        return slot;
    }

    private int getFlintSlot() {
        if (mc.player.getMainHandStack().getItem() == Items.FLINT_AND_STEEL)
            return mc.player.getInventory().selectedSlot;

        int slot = -1;

        slot = InventoryUtil.findItemInventorySlot(Items.FLINT_AND_STEEL, false);
        return slot;
    }

    private List<BlockPos> getAffectedBlocks(PlayerEntity pl) {
        List<BlockPos> tempPos = new ArrayList<>();
        List<BlockPos> finalPos = new ArrayList<>();
        List<Box> boxes = new ArrayList<>();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.squaredDistanceTo(pl) < 9 && player != pl)
                boxes.add(player.getBoundingBox());
        }

        boxes.add(pl.getBoundingBox());

        BlockPos center = getPlayerPos(pl);

        tempPos.add(center);
        tempPos.add(center.north());
        tempPos.add(center.north().east());
        tempPos.add(center.west());
        tempPos.add(center.west().north());
        tempPos.add(center.south());
        tempPos.add(center.south().west());
        tempPos.add(center.east());
        tempPos.add(center.east().south());

        for (BlockPos bp : tempPos)
            if (new Box(bp).intersects(pl.getBoundingBox()))
                finalPos.add(bp);

        for (BlockPos bp : Lists.newArrayList(finalPos)) {
            for (Box box : boxes) {
                if (new Box(bp).intersects(box))
                    finalPos.add(BlockPos.ofFloored(box.getCenter()));
            }
        }

        return finalPos;
    }

    private BlockPos getPlayerPos(@NotNull PlayerEntity pl) {
        return BlockPos.ofFloored(pl.getX(), pl.getY() - Math.floor(pl.getY()) > 0.8 ? Math.floor(pl.getY()) + 1.0 : Math.floor(pl.getY()), pl.getZ());
    }


    public enum RenderMode {
        Fade, Decrease
    }

    public enum PlaceMode {
        Packet,
        Normal
    }
}
