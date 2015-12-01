package com.estebes.xtbxlib.ic2.item.tool;

import com.estebes.xtbxlib.util.Util;
import ic2.api.item.ElectricItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

@SuppressWarnings("all")
public class ItemElectricToolDrillArea extends ItemElectricToolDrill {

	public ItemElectricToolDrillArea(String itemName, ItemElectricToolType toolType, boolean providesEnergy, double maxEnergy,
									int energyTier, double operationCost, float miningSpeed) {
		super(itemName, toolType, providesEnergy, maxEnergy, energyTier, operationCost, miningSpeed);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		Block block = player.worldObj.getBlock(x, y, z);
		if (block == null || !canHarvestBlock(block, stack)) {
			return super.onBlockStartBreak(stack, x, y, z, player);
		}
		MovingObjectPosition mop = Util.raytraceFromEntity(player.worldObj, player, false, 4.5D);
		if (mop == null) {
			return super.onBlockStartBreak(stack, x, y, z, player);
		}
		int sideHit = mop.sideHit;

		int xRange = 1;
		int yRange = 1;
		int zRange = 1;
		switch (sideHit) {
			case 0:
			case 1:
				yRange = 0;
				zRange = 1;
				break;
			case 2:
			case 3:
				xRange = 1;
				zRange = 0;
				break;
			case 4:
			case 5:
				xRange = 0;
				zRange = 1;
				break;
		}

		for (int xPos = x - xRange; xPos <= x + xRange; xPos++) {
			for (int yPos = y - yRange; yPos <= y + yRange; yPos++) {
				for (int zPos = z - zRange; zPos <= z + zRange; zPos++) {
					if (ElectricItem.manager.canUse(stack, this.operationCost)) {
						if (xPos == x && yPos == y && zPos == z) {
							continue;
						}
						if (!super.onBlockStartBreak(stack, xPos, yPos, zPos, player)) {
							breakExtraBlock(player.worldObj, xPos, yPos, zPos, player, stack);
							ElectricItem.manager.use(stack, this.operationCost, player);
						}
					}
				}
			}
		}
		return super.onBlockStartBreak(stack, x, y, z, player);
	}

	public void breakExtraBlock(World world, int x, int y, int z, EntityPlayer entityPlayer, ItemStack itemStack) {
		if (world.isAirBlock(x, y, z) || !(entityPlayer instanceof EntityPlayerMP)) {
			return;
		}

		EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entityPlayer;

		Block block = world.getBlock(x, y, z);
		int metaData = world.getBlockMetadata(x, y, z);

		if (!canHarvestBlock(block, itemStack) || !(block.getBlockHardness(world, x, y, z) > 0.0F)) {
			return;
		}

		BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world, entityPlayerMP.theItemInWorldManager.getGameType(), entityPlayerMP, x,y,z);
		if(event.isCanceled()) {
			return;
		}

		if (entityPlayerMP.capabilities.isCreativeMode) {
			block.onBlockHarvested(world, x, y, z, metaData, entityPlayerMP);
			if (block.removedByPlayer(world, entityPlayerMP, x, y, z, false)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, metaData);
			}

			if (!world.isRemote) {
				entityPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
			}
			return;
		}

		if (!world.isRemote) {
			block.onBlockHarvested(world, x,y,z, metaData, entityPlayerMP);
			if(block.removedByPlayer(world, entityPlayerMP, x,y,z, true)) {
				block.onBlockDestroyedByPlayer( world, x,y,z, metaData);
				block.harvestBlock(world, entityPlayerMP, x,y,z, metaData);
				block.dropXpOnBlockBreak(world, x,y,z, event.getExpToDrop());
			}
			entityPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
		}
		else {
			world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (metaData << 12));
			if(block.removedByPlayer(world, entityPlayerMP, x,y,z, true)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, metaData);
			}
		}
	}
}
