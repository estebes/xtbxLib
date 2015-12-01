package com.estebes.xtbxlib.ic2.item.tool;

import com.estebes.xtbxlib.ic2.item.tool.ItemElectricToolType;
import com.estebes.xtbxlib.reference.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IElectricItem;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

@SuppressWarnings("all")
public class ItemElectricTool extends ItemTool implements IElectricItem, IBoxable {
    protected final String itemName;
	protected final ItemElectricToolType toolType;
    protected final boolean providesEnergy;
    protected final double maxEnergy;
    protected final int energyTier;
	protected final double operationCost;
	protected final float miningSpeed;

	/**
	 *
	 * @param itemName
	 * @param toolType
	 * @param providesEnergy
	 * @param maxEnergy
	 * @param energyTier
	 * @param operationCost
	 * @param miningSpeed
	 */
    public ItemElectricTool(String itemName, ItemElectricToolType toolType, boolean providesEnergy,
							   double maxEnergy, int energyTier, double operationCost, float miningSpeed) {
        super(0.0F, toolType.getToolMaterial(), new HashSet());

		this.itemName = itemName;
		this.toolType = toolType;
		this.providesEnergy = providesEnergy;
		this.maxEnergy = maxEnergy;
		this.energyTier = energyTier;
		this.operationCost = operationCost;
		this.miningSpeed = miningSpeed;

		this.setUnlocalizedName(itemName);
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }


    // -------------- Item -------------- //

    @Override
    public boolean canHarvestBlock(Block block, ItemStack itemStack) {
        return this.toolType.canHarvestBlock(block, itemStack);
    }

	@Override
	public float func_150893_a(ItemStack itemStack, Block block) {
		if (this.toolType.canHarvestBlock(block, itemStack)) {
			return this.efficiencyOnProperMaterial;
		}
	return super.func_150893_a(itemStack, block);
	}

	@Override
	public float getDigSpeed(ItemStack itemStack, Block block, int metaData) {
		if (canHarvestBlock(block, itemStack) && ElectricItem.manager.canUse(itemStack, this.operationCost)) {
			return this.miningSpeed;
		}
		return 1.0F;
	}

	@Override
	public int getHarvestLevel(ItemStack itemStack, String toolClass) {
		if (this.toolType.harvestLevelChecker(toolClass)) {
			return this.toolMaterial.getHarvestLevel();
		}
		return super.getHarvestLevel(itemStack, toolClass);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemStack, World world, Block block, int x, int y, int z, EntityLivingBase entityLiving) {
		if (block.getBlockHardness(world, x, y, z) != 0.0D) {
			if (entityLiving != null) {
				ElectricItem.manager.use(itemStack, this.operationCost, entityLiving);
			}
			else {
				ElectricItem.manager.discharge(itemStack, this.operationCost, this.getTier(itemStack), true, false, false);
			}
		}
		return true;
	}

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemStack1, ItemStack itemStack2) {
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase entityliving1) {
        return true;
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack) {
        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        return ElectricItem.manager.getCharge(itemStack) / this.maxEnergy * 100.0D;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        // Charged Item
        ItemStack chargedItem = new ItemStack(this);
        ElectricItem.manager.charge(chargedItem, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, false);
        itemList.add(chargedItem);

        // Depleted Item
        ItemStack depletedItem = new ItemStack(this);
        itemList.add(depletedItem);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(Reference.LOWERCASE_MOD_ID + ":" + this.itemName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack itemStack, int pass) {
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemStack, int pass) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean var) {
        list.add(StatCollector.translateToLocal("tooltip.tool.PowerTier") + " " + this.energyTier);
    }


    /* ---------- IElectricItem ---------- */

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return this.providesEnergy;
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return this.maxEnergy;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return this.energyTier;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return 32.0D * Math.pow(4.0D, this.energyTier);
    }


    /* ---------- IBoxable ---------- */

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemStack) {
        return true;
    }
}
