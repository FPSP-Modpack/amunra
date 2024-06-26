package de.katzenpapst.amunra.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import micdoodle8.mods.galacticraft.api.item.IItemElectric;
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific;

public class ContainerArtificalGravity extends ContainerWithPlayerInventory {

    public ContainerArtificalGravity(InventoryPlayer playerInv, IInventory tile) {
        super(tile);

        this.addSlotToContainer(new SlotSpecific(tile, 0, 152, 132, IItemElectric.class));

        initPlayerInventorySlots(playerInv, 35);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        TileEntity te = (TileEntity) this.tileEntity;
        return player.getDistanceSq((double) te.xCoord + 0.5D, (double) te.yCoord + 0.5D, (double) te.zCoord + 0.5D)
            <= 64.0D;
    }

}
