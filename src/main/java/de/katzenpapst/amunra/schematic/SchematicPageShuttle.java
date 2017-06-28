package de.katzenpapst.amunra.schematic;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.gui.schematic.GuiSchematicShuttle;
import de.katzenpapst.amunra.inventory.schematic.ContainerSchematicShuttle;
import de.katzenpapst.amunra.item.ARItems;
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class SchematicPageShuttle implements ISchematicPage {

    public SchematicPageShuttle() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getPageID() {
        return AmunRa.config.schematicIdShuttle;
    }

    @Override
    public int getGuiID()
    {
        // this is not a regular GUI id. It gets sent to GC, and then if it can't handle the stuff itself, it uses
        // getResultScreen and getResultContainer
       return AmunRa.config.guiIdShuttle;
    }

    @Override
    public ItemStack getRequiredItem()
    {
        return ARItems.shuttleSchematic.getItemStack(1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getResultScreen(EntityPlayer player, BlockPos pos)
    {
        return new GuiSchematicShuttle(player.inventory, pos);
    }

    @Override
    public Container getResultContainer(EntityPlayer player, BlockPos pos)
    {
        return new ContainerSchematicShuttle(player.inventory, pos);
    }

    @Override
    public int compareTo(ISchematicPage o)
    {
        if (this.getPageID() > o.getPageID())
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

}
