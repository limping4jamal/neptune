package me.neptune.module.modules.combat;

import me.neptune.gui.Color;
import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import me.neptune.settings.Setting;
import me.neptune.settings.SliderSetting;
import me.neptune.utils.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class AnchorAura extends Module {

	public static AnchorAura INSTANCE;
	private final BooleanSetting spam =
			new BooleanSetting("Spam", "anchoraura_spam",false);
	private final BooleanSetting rotate =
			new BooleanSetting("Rotate", "anchoraura_rotate",true);
	private final BooleanSetting eatingPause =
			new BooleanSetting("EatingPause", "anchoraura_eatingpause",true);
	private final SliderSetting range =
			new SliderSetting("Range", "anchoraura_range",5.0, 0.0, 6.0, 0.1);
	private final SliderSetting minDamage =
			new SliderSetting("MinDamage", "anchoraura_mindmg",6.0, 0.0, 36.0, 0.1);
	private final SliderSetting maxSelfDamage =
			new SliderSetting("MaxSelfDamage", "anchoraura_maxdmg",6.0, 0.0, 36.0, 0.1);
	private final SliderSetting predictTicks =
			new SliderSetting("PredictTicks", "anchoraura_predictticks",2, 0.0, 10, 1);
	private final SliderSetting delay =
			new SliderSetting("Delay", "anchoraura_delay",0.0, 0.0, 0.5, 0.01);
	private final SliderSetting spamDelay =
			new SliderSetting("SpamDelay", "anchoraura_delay", 0.0, 0.0, 0.5, 0.01);
	private final SliderSetting charge =
			new SliderSetting("Charges", "anchoraura_charge", 1, 1, 4, 1);
	private final BooleanSetting terrainIgnore =
			new BooleanSetting("TerrainIgnore", "anchoraura_terrianignore",false);
	private final BooleanSetting antiStepOut =
			new BooleanSetting("AntiStepOut", "anchoraura_antistepout", false);
	private final BooleanSetting invisible =
			new BooleanSetting("SpamInvisible", "anchoraura_invisible", false);
	private final Timer timer = new Timer().reset();
	public AnchorAura() {
		super("AnchorAura", Category.Combat);
		INSTANCE = this;
		try {
			for (Field field : AnchorAura.class.getDeclaredFields()) {
				if (!Setting.class.isAssignableFrom(field.getType()))
					continue;
				Setting setting = (Setting) field.get(this);
				addSetting(setting);
			}
		} catch (Exception e) {
		}
	}
	private final ArrayList<BlockPos> chargeList = new ArrayList<>();
	public static BlockPos placePos;
	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		if (placePos != null) {
			this.getRenderUtils().draw3DBox(matrixStack, new Box(placePos), new Color(255, 255, 255), 0.2f);
		}
	}
	@Override
	public void onUpdate() {
		int anchor;
		int glowstone;
		int unBlock;
		int old = mc.player.getInventory().selectedSlot;
		if ((anchor = InventoryUtil.findBlock(Blocks.RESPAWN_ANCHOR)) == -1) {
            return;
		}
		if ((glowstone = InventoryUtil.findBlock(Blocks.GLOWSTONE)) == -1) {
            return;
		}
		if ((unBlock = InventoryUtil.findUnBlock()) == -1) {
            return;
		}
		if (mc.player.isSneaking()) {
            return;
		}
		if (eatingPause.getValue() && mc.player.isUsingItem()) {
            return;
		}
		if (spam.getValue()){
			if (!timer.passed((long) (spamDelay.getValueFloat() * 750))) {
				return;
			}
		} else{
			if (!timer.passed((long) (delay.getValueFloat() * 750))) {
				return;
			}
		}
		placePos = null;
		float bestDamage = minDamage.getValueFloat();
		float bestAnchorDamage = minDamage.getValueFloat();
		for (BlockPos pos : BlockUtil.getSphere(range.getValueFloat())) {
			for (PlayerEntity player : CombatUtil.getEnemies(10)) {
				if (CombatUtil.getAnchorDamage(pos, mc.player, 0, false) > maxSelfDamage.getValueFloat()) {
					continue;
				}
				if (BlockUtil.getBlock(pos) != Blocks.RESPAWN_ANCHOR) {
					if (bestAnchorDamage > minDamage.getValueFloat()) continue;
					if (!BlockUtil.canPlace(pos, range.getValue())) continue;
					float damage = CombatUtil.getAnchorDamage(pos, player, predictTicks.getValueInt(), terrainIgnore.getValue());
					if (damage > bestDamage) {
						bestDamage = damage;
						placePos = pos;
					}
				} else {
					float damage = CombatUtil.getAnchorDamage(pos, player, predictTicks.getValueInt(), terrainIgnore.getValue());
					if (damage > bestAnchorDamage) {
						bestAnchorDamage = damage;
						placePos = pos;
					}
				}
			}
		}
		if (placePos != null){
//			return;
//		}else{
			if (spam.getValue()) {
				// 换锚
				InventoryUtil.doSwap(anchor);
				// 放锚
				BlockUtil.placeBlock(placePos, rotate.getValue());
				// 换萤石
				InventoryUtil.doSwap(glowstone);
				// 充能
				for(int i=charge.getValueInt(); i<=charge.getValueInt()+charge.getValueInt()-1; i++){
					BlockUtil.clickBlock(placePos, BlockUtil.getClickSide(placePos), rotate.getValue());
				}
				// 换锚
				InventoryUtil.doSwap(anchor);
				// 炸！
				BlockUtil.clickBlock(placePos, BlockUtil.getClickSide(placePos), rotate.getValue());
				if(!invisible.getValue()){
					BlockUtil.placeBlock(placePos, rotate.getValue());
					BlockUtil.placeBlock(placePos, rotate.getValue());
				}
			} else {
				if (BlockUtil.canPlace(placePos, range.getValue())) {
					// 换锚
					InventoryUtil.doSwap(anchor);
					// 放锚
					BlockUtil.placeBlock(placePos, rotate.getValue());
				} else if (BlockUtil.getBlock(placePos) == Blocks.RESPAWN_ANCHOR) {
					if (!chargeList.contains(placePos)) {
						// 换萤石
						InventoryUtil.doSwap(glowstone);
						// 充能
						for(int i=charge.getValueInt(); i<=charge.getValueInt()+charge.getValueInt()-1; i++){
							BlockUtil.clickBlock(placePos, BlockUtil.getClickSide(placePos), rotate.getValue());
						}
						chargeList.add(placePos);
					} else {
						chargeList.remove(placePos);
						// 换锚
						InventoryUtil.doSwap(anchor);
						// 炸！
						BlockUtil.clickBlock(placePos, BlockUtil.getClickSide(placePos), rotate.getValue());
						if(!invisible.getValue()){
							BlockUtil.placeBlock(placePos, rotate.getValue());
							BlockUtil.placeBlock(placePos, rotate.getValue());
						}
					}
				}
			}
			if(CombatUtil.isAnchorBroke(placePos) && antiStepOut.getValue()){
				// 换锚
				InventoryUtil.doSwap(anchor);
				// 放锚
				BlockUtil.placeBlock(placePos, rotate.getValue());
			}
			timer.reset();
			// 换原来的位置
			InventoryUtil.doSwap(old);
		}
    }
}