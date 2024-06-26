package de.katzenpapst.amunra.block;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.helper.BlockMassHelper;
import de.katzenpapst.amunra.item.ItemSlabMulti;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;

public class BlockSlabMeta extends BlockSlab implements IMetaBlock, IMassiveBlock {

    protected HashMap<String, Integer> nameMetaMap = null;
    protected SubBlock[] subBlocksArray = new SubBlock[8];
    protected BlockDoubleslabMeta doubleslabMetablock;

    public BlockSlabMeta(String name, Material material) {
        // I think the first parameter is true for doubleslabs...
        super(false, material);
        setBlockName(name);
        nameMetaMap = new HashMap<String, Integer>();
    }

    @Override
    public String getUnlocalizedSubBlockName(int meta) {
        final SubBlock sb = this.getSubBlock(meta);
        if (sb != null) {
            return sb.getUnlocalizedName() + ".slab";
        }
        return this.getUnlocalizedName() + ".slab";
    }

    public void setDoubleslabMeta(BlockDoubleslabMeta doubleslabMetablock) {
        this.doubleslabMetablock = doubleslabMetablock;
    }

    @Override
    public BlockMetaPair addSubBlock(int meta, SubBlock sb) {
        if (meta >= subBlocksArray.length || meta < 0) {
            throw new IllegalArgumentException(
                "Meta " + meta + " must be <= " + (subBlocksArray.length - 1) + " && >= 0");
        }

        if (subBlocksArray[meta] != null) {
            throw new IllegalArgumentException("Meta " + meta + " is already in use");
        }

        if (nameMetaMap.get(sb.getUnlocalizedName()) != null) {
            throw new IllegalArgumentException("Name " + sb.getUnlocalizedName() + " is already in use");
        }
        // sb.setParent(this);
        nameMetaMap.put(sb.getUnlocalizedName(), meta);
        subBlocksArray[meta] = sb;
        return new BlockMetaPair(this, (byte) meta);
    }

    public BlockMetaPair addSubBlock(int meta, BlockMetaPair basedOn) {

        return addSubBlock(meta, ((IMetaBlock) basedOn.getBlock()).getSubBlock(basedOn.getMetadata()));
    }

    @Override
    public int getMetaByName(String name) {
        Integer i = nameMetaMap.get(name);
        if (i == null) {
            throw new IllegalArgumentException("Subblock " + name + " doesn't exist");
        }
        return i;
    }

    @Override
    public SubBlock getSubBlock(int meta) {

        return subBlocksArray[getDistinctionMeta(meta)];
    }

    public SubBlock[] getAllSubBlocks() {
        return subBlocksArray;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        final SubBlock sb = this.getSubBlock(meta);
        if (sb != null) {
            return sb.getIcon(side, 0);
        }
        return super.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        for (SubBlock sb : subBlocksArray) {
            if (sb != null) {
                sb.registerBlockIcons(par1IconRegister);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        return AmunRa.arTab;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int damageDropped(int meta) {
        return this.getDistinctionMeta(meta);
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 1;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 0; i < this.subBlocksArray.length; i++) {
            if (subBlocksArray[i] != null) {
                par3List.add(new ItemStack(par1, 1, i));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (getSubBlock(meta) != null) {
            return new ItemStack(Item.getItemFromBlock(this), 1, getDistinctionMeta(meta));
        }

        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public void register() {
        // doubleslabMetablock
        GameRegistry.registerBlock(this, ItemSlabMulti.class, this.getUnlocalizedName(), this, doubleslabMetablock);

        for (int i = 0; i < subBlocksArray.length; i++) {
            SubBlock sb = subBlocksArray[i];
            if (sb != null) {

                this.setHarvestLevel(sb.getHarvestTool(0), sb.getHarvestLevel(0), i);
            }
        }
    }

    @Override
    public String func_150002_b(int p_150002_1_) {
        // something like getNameByMeta
        // net.minecraft.item.ItemSlab calls this
        final SubBlock sb = this.getSubBlock(p_150002_1_);
        if (sb != null) {
            return this.getUnlocalizedName() + "." + sb.getUnlocalizedName();
        }
        return this.getUnlocalizedName();
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX,
        double explosionY, double explosionZ) {
        final SubBlock sb = this.getSubBlock(world.getBlockMetadata(x, y, z));
        if (sb != null) {
            return sb.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
        }
        return super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        final SubBlock sb = this.getSubBlock(worldIn.getBlockMetadata(x, y, z));
        if (sb != null) {
            return sb.getBlockHardness(worldIn, x, y, z);
        }
        return super.getBlockHardness(worldIn, x, y, z);
    }

    @Override
    public int getNumPossibleSubBlocks() {
        return subBlocksArray.length;
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
        final SubBlock sb = this.getSubBlock(metadata);
        if (sb != null) {
            return sb.getExpDrop(world, 0, fortune);
        }
        return super.getExpDrop(world, metadata, fortune);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        final SubBlock sb = this.getSubBlock(worldIn.getBlockMetadata(x, y, z));
        if (sb != null) {
            sb.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        }
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
    }

    @Override
    public float getMass(World w, int x, int y, int z, int meta) {
        // return half the mass, because slab
        return BlockMassHelper.getBlockMass(w, this.getSubBlock(meta), meta, x, y, z) / 2.0f;
    }

    /**
     * Queries the class of tool required to harvest this block, if null is returned
     * we assume that anything can harvest this block.
     *
     * @param metadata
     * @return
     */
    @Override
    public String getHarvestTool(int metadata) {
        final SubBlock sb = this.getSubBlock(metadata);
        return sb == null ? null : sb.getHarvestTool(metadata);
    }

    /**
     * Queries the harvest level of this item stack for the specifred tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack This item stack instance
     * @return Harvest level, or -1 if not the specified tool type.
     */
    @Override
    public int getHarvestLevel(int metadata) {
        final SubBlock sb = this.getSubBlock(metadata);
        return sb == null ? -1 : sb.getHarvestLevel(metadata);
    }

    /**
     * Checks if the specified tool type is efficient on this block,
     * meaning that it digs at full speed.
     *
     * @param type
     * @param metadata
     * @return
     */
    @Override
    public boolean isToolEffective(String type, int metadata) {
        return this.getHarvestTool(metadata)
            .equals(type);
    }
}
