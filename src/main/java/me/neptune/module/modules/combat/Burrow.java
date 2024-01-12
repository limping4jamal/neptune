/**
 * Anti-Invis Module
 */
package me.neptune.module.modules.combat;

import me.neptune.cmd.CommandManager;
import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.Setting;
import me.neptune.utils.BlockUtil;
import me.neptune.utils.InventoryUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.*;

import java.lang.reflect.Field;

public class Burrow extends Module {
	public static Burrow INSTANCE;
	private final BooleanSetting rotate =
			new BooleanSetting("Rotate", "burrow_rotate",true);
	private final BooleanSetting multiPlace =
			new BooleanSetting("MultiPlace", "burrow_multiplace", false);
	public Burrow() {
		super("Burrow", Category.Combat);
		INSTANCE = this;
		try {
			for (Field field : Burrow.class.getDeclaredFields()) {
				if (!Setting.class.isAssignableFrom(field.getType()))
					continue;
				Setting setting = (Setting) field.get(this);
				addSetting(setting);
			}
		} catch (Exception e) {
		}
	}
	public Boolean isInAir(){
		Vec3d downPos =
				BlockUtil.getPlayerPos().down(1).toCenterPos();
		BlockPos downPos1 =
				BlockPos.ofFloored(downPos.add(1, 0, 1));
		BlockPos downPos2 =
				BlockPos.ofFloored(downPos.add(-0.3, 0, -1));
		BlockPos downPos3 =
				BlockPos.ofFloored(downPos.add(-1, 0, 1));
		BlockPos downPos4 =
				BlockPos.ofFloored(downPos.add(1, 0, -1));
		if(mc.world.getBlockState(downPos1).getBlock() == Blocks.AIR){
			return true;
		}else if(mc.world.getBlockState(downPos2).getBlock() == Blocks.AIR){
			return true;
		}else if(mc.world.getBlockState(downPos3).getBlock() == Blocks.AIR){
			return true;
		}else if(mc.world.getBlockState(downPos4).getBlock() == Blocks.AIR){
			return true;
		}
        return false;
    }
	@Override	
	public void onUpdate() {

		if (!mc.player.isOnGround()) {
		}
		int obsidian = InventoryUtil.findBlock(Blocks.OBSIDIAN);
		int old = mc.player.getInventory().selectedSlot;
		if (obsidian == -1) {
			CommandManager.sendChatMessage("No Burrow Blocks");
			disable();
		}
		Vec3d currentPos = mc.player.getPos();
		BlockPos pos1 = new BlockPos(MathHelper.floor(mc.player.getX() + 0.2), MathHelper.floor(mc.player.getY() + 0.5), MathHelper.floor(mc.player.getZ() + 0.2));
		if (!canPlace(pos1)) {
			//CommandManager.sendChatMessage("Finish");
			disable();
		}
		if(isInAir()){
			BlockUtil.placeBlock(
					BlockPos.ofFloored(currentPos.x + 1, currentPos.y - 1, currentPos.z + 1), rotate.getValue());
			BlockUtil.placeBlock(
					BlockPos.ofFloored(currentPos.x  - 1, currentPos.y - 1, currentPos.z  - 1), rotate.getValue());
			BlockUtil.placeBlock(
					BlockPos.ofFloored(currentPos.x  - 1, currentPos.y - 1, currentPos.z + 1), rotate.getValue());
			BlockUtil.placeBlock(
					BlockPos.ofFloored(currentPos.x + 1, currentPos.y - 1, currentPos.z  - 1), rotate.getValue());
		}

		mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.4199999868869781, mc.player.getZ(), false));
		mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.7531999805212017, mc.player.getZ(), false));
		mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.9999957640154541, mc.player.getZ(), false));
		mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 1.1661092609382138, mc.player.getZ(), false));
		InventoryUtil.doSwap(obsidian);
		if (canPlace(pos1) && !multiPlace.getValue()) {
			BlockUtil.placeBlock(pos1, rotate.getValue());
			BlockUtil.placeBlock(
					BlockPos.ofFloored(currentPos.x, currentPos.y-1, currentPos.z), rotate.getValue());
		} else if (multiPlace.getValue()){
			BlockUtil.placeBlock(
					BlockPos.ofFloored(currentPos.x + 0.3, currentPos.y, currentPos.z + 0.3), rotate.getValue());
			BlockUtil.placeBlock(
					BlockPos.ofFloored(currentPos.x  - 0.3, currentPos.y, currentPos.z  - 0.3), rotate.getValue());
			BlockUtil.placeBlock(
					BlockPos.ofFloored(currentPos.x  - 0.3, currentPos.y, currentPos.z + 0.3), rotate.getValue());
			BlockUtil.placeBlock(
					BlockPos.ofFloored(currentPos.x + 0.3, currentPos.y, currentPos.z  - 0.3), rotate.getValue());

			InventoryUtil.doSwap(old);
			if (mc.player.getY() >= 3) {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
						mc.player.getX() + 0.4,mc.player.getY() + 3,mc.player.getZ() + 0.4, false
				));
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
						mc.player.getX(),mc.player.getY()+ 3,mc.player.getZ(), false
				));
			}
		}
		InventoryUtil.doSwap(old);
		mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 3.3400880035762786, mc.player.getZ(), false));
		mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 1.0, mc.player.getZ(), false));
		double distance = 0;
		BlockPos bestPos = null;
		for (BlockPos pos : BlockUtil.getSphere(6)) {
			if (!canGoto(pos))
				continue;
			if (bestPos == null || mc.player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < distance) {
				bestPos = pos;
				distance = mc.player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			}
		}
		if (bestPos != null) {
			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(bestPos.getX() + 0.5, bestPos.getY(), bestPos.getZ() + 0.5, false));
		}
		disable();
	}
	
	private boolean canGoto(BlockPos pos) {
		return !BlockUtil.getState(pos).blocksMovement() && !BlockUtil.getState(pos.up()).blocksMovement();
	}

	private boolean canPlace(BlockPos pos) {
		if (!BlockUtil.canBlockFacing(pos)) {
			//CommandManager.sendChatMessage("facing false");
			return false;
		}
		if (!BlockUtil.canReplace(pos)) {
			//CommandManager.sendChatMessage("replace false");
			return false;
		}
		return !hasEntity(pos);
	}

	private boolean hasEntity(BlockPos pos) {
		for (Entity entity : mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
			if (entity == mc.player) continue;
			if (!entity.isAlive() || entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ExperienceBottleEntity || entity instanceof ArrowEntity || entity instanceof EndCrystalEntity)
				continue;
			return true;
		}
		return false;
	}
}