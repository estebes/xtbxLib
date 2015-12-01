package com.estebes.xtbxlib.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemDefault extends Item {
	protected String itemName;

	public ItemDefault(String itemName, int maxStackSize) {
		this.itemName = itemName;

		this.maxStackSize = maxStackSize;
		this.setUnlocalizedName(this.itemName);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("" + ":" + this.itemName);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack itemStack, int pass) {
		return this.itemIcon;
	}
}
