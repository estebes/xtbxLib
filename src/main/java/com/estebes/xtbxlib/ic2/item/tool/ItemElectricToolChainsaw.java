package com.estebes.xtbxlib.ic2.item.tool;

import com.estebes.xtbxlib.util.Util;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.api.item.ElectricItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import java.util.ArrayList;

@SuppressWarnings("all")
public class ItemElectricToolChainsaw extends ItemElectricTool {
	public ItemElectricToolChainsaw(String itemName, ItemElectricToolType toolType, boolean providesEnergy,
									double maxEnergy, int energyTier, double operationCost, float miningSpeed) {
		super(itemName, toolType, providesEnergy, maxEnergy, energyTier, operationCost, miningSpeed);

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event)
	{
		if (!Util.isServerSide()) {
			return;
		}
		Entity entity = event.target;
		EntityPlayer player = event.entityPlayer;
		ItemStack itemstack = player.inventory.getStackInSlot(player.inventory.currentItem);
		if ((itemstack != null) && (itemstack.getItem() == this) && ((entity instanceof IShearable)) && (ElectricItem.manager.use(itemstack, this.operationCost, player))) {
			IShearable target = (IShearable)entity;
			if (target.isShearable(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ)) {
				ArrayList<ItemStack> drops = target.onSheared(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
				for (ItemStack stack : drops) {
					EntityItem ent = entity.entityDropItem(stack, 1.0F);
					ent.motionY += itemRand.nextFloat() * 0.05F;
					ent.motionX += (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F;
					ent.motionZ += (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F;
				}
			}
		}
	}
}
