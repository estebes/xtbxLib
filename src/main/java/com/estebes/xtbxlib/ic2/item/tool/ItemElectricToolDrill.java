package com.estebes.xtbxlib.ic2.item.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Locale;

public class ItemElectricToolDrill extends ItemElectricTool {

	public ItemElectricToolDrill(String itemName, ItemElectricToolType toolType, boolean providesEnergy, double maxEnergy,
									int energyTier, double operationCost, float miningSpeed) {
		super(itemName, toolType, providesEnergy, maxEnergy, energyTier, operationCost, miningSpeed);
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
		for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
			ItemStack torchStack = entityPlayer.inventory.mainInventory[i];
			if ((torchStack != null) && (torchStack.getUnlocalizedName().toLowerCase(Locale.ENGLISH).contains("torch"))) {
				Item item = torchStack.getItem();
				if ((item instanceof ItemBlock)) {
					int oldMeta = torchStack.getItemDamage();
					int oldSize = torchStack.stackSize;
					boolean result = torchStack.tryPlaceItemIntoWorld(entityPlayer, world, x, y, z, side, xOffset, yOffset, zOffset);
					if (entityPlayer.capabilities.isCreativeMode) {
						torchStack.setItemDamage(oldMeta);
						torchStack.stackSize = oldSize;
					}
					else if (torchStack.stackSize <= 0) {
						ForgeEventFactory.onPlayerDestroyItem(entityPlayer, torchStack);
						entityPlayer.inventory.mainInventory[i] = null;
					}
					if (result) {
						return true;
					}
				}
			}
		}
		return super.onItemUse(itemStack, entityPlayer, world, x, y, z, side, xOffset, yOffset, zOffset);
	}
}
