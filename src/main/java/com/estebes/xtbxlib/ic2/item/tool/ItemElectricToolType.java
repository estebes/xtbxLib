package com.estebes.xtbxlib.ic2.item.tool;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("all")
public enum ItemElectricToolType {
    // Iron mining level drill (iron pickaxe + iron shovel)
    TOOL_DRILL_IRON(Item.ToolMaterial.IRON, new Object[] { Items.iron_pickaxe, Items.iron_shovel }),

    // Diamond mining level drill (diamond pickaxe + diamond shovel)
    TOOL_DRILL_DIAMOND(Item.ToolMaterial.EMERALD, new Object[] { Items.diamond_pickaxe, Items.diamond_shovel }),

    // Chainsaw (diamond axe + shears)
    TOOL_CHAINSAW(Item.ToolMaterial.IRON, new Object[] { Items.diamond_axe, Items.shears }),

    // Weapon (diamond sword)
    TOOL_WEAPON(Item.ToolMaterial.EMERALD, Items.diamond_sword);

    private String name;
    private final Item.ToolMaterial toolMaterial;
    private final Set<Object> toolProperties;

	private ItemElectricToolType(Item.ToolMaterial toolMaterial, Object... toolProperties) {
        this.toolMaterial = toolMaterial;
        this.toolProperties = new HashSet(Arrays.asList(toolProperties));
    }

    public Item.ToolMaterial getToolMaterial() {
        return this.toolMaterial;
    }

	/**
	 * Check if the tool can harvest the block.
     * @param block block to be harvested.
     * @param itemStack tool being used.
     * @return true if block can be harvested and false otherwise.
     */
    public boolean canHarvestBlock(Block block, ItemStack itemStack) {
        for (Object object : toolProperties) {
            if (object instanceof ItemTool) {
                try {
                    Class itemClass = object.getClass();
                    Method methodBlock = itemClass.getMethod("canHarvestBlock", new Class[] { Block.class, ItemStack.class });
                    Method methodMaterial = itemClass.getMethod("func_150893_a", new Class[] { ItemStack.class, Block.class });
                    if ((Boolean) methodBlock.invoke(object, new Object[] { block, itemStack })) {
                        return true;
                    }
                    if ((Float) methodMaterial.invoke(object, new Object[] { itemStack, block }) > 1.0F && block.getMaterial().isToolNotRequired()) {
                        return true;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean harvestLevelChecker(String toolClass) {
		for (Object object : toolProperties) {
			if (object instanceof ItemTool) {
				try {
					Class itemClass = object.getClass();
					Field fieldToolClass = itemClass.getDeclaredField("toolClass");
					fieldToolClass.setAccessible(true);
					if (toolClass.equals(fieldToolClass.get(object))) {
						return true;
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
