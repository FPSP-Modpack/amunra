package de.katzenpapst.amunra.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.item.ItemBlockMulti;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;

public class BlockLeafMeta extends BlockLeaves implements IMetaBlock {

    public static String[] unlocLeafNames = null;
    protected HashMap<String, Integer> nameMetaMap = null;
    protected SubBlock[] subBlocksArray = new SubBlock[4];

    public BlockLeafMeta(Material mat, boolean gfxMode) {
        super();
        nameMetaMap = new HashMap<String, Integer>();
        this.setLightOpacity(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockColor() {
        return 16777215;
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return 16777215;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return 16777215;
    }
    // net.minecraft.client.renderer.RenderGlobal.loadRenderers()

    @Override
    public BlockMetaPair addSubBlock(int meta, SubBlock sb) {
        if (!(sb instanceof SubBlockLeaf)) {
            throw new IllegalArgumentException("SubBlocks need to be instanceof SubBlockLeaf");
        }
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
        sb.setParent(this);
        nameMetaMap.put(sb.getUnlocalizedName(), meta);
        subBlocksArray[meta] = sb;
        return new BlockMetaPair(this, (byte) meta);
    }

    @Override
    public int getMetaByName(String name) {
        if (this.nameMetaMap.containsKey(name)) {
            return this.nameMetaMap.get(name);
        }
        throw new IllegalArgumentException("Subblock " + name + " doesn't exist");
    }

    @Override
    public SubBlock getSubBlock(int meta) {
        // this works like wood now
        meta = getDistinctionMeta(meta);
        return subBlocksArray[meta];
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        final SubBlock sb = this.getSubBlock(meta);
        if (sb == null) {
            return null;
        }
        if (isOpaqueCube()) {
            return ((SubBlockLeaf) sb).getOpaqueIcon(side);
        }
        return sb.getIcon(side, 0);
    }

    /**
     * Seems to return all unlocalized names
     */
    @Override
    public String[] func_150125_e() {
        if (unlocLeafNames == null) {
            unlocLeafNames = new String[nameMetaMap.size()];
            for (Map.Entry<String, Integer> entry : nameMetaMap.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();
                unlocLeafNames[value] = key;
            }
        }
        return unlocLeafNames;
    }

    @Override
    public void register() {
        GameRegistry.registerBlock(this, ItemBlockMulti.class, this.getUnlocalizedName());

        for (int i = 0; i < subBlocksArray.length; i++) {
            SubBlock sb = subBlocksArray[i];
            if (sb != null) {

                this.setHarvestLevel(sb.getHarvestTool(0), sb.getHarvestLevel(0), i);
            }
        }

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
        SubBlock sb = getSubBlock(meta);

        if (sb == null || sb.dropsSelf()) {
            return Item.getItemFromBlock(this);
        }
        return sb.getItemDropped(0, random, fortune);
    }

    @Override
    public int damageDropped(int meta) {
        SubBlock sb = getSubBlock(meta);
        if (sb == null || sb.dropsSelf()) {
            return meta;
        }
        return sb.damageDropped(0);
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        SubBlock sb = getSubBlock(meta);
        if (sb == null || sb.dropsSelf()) {
            return 1;
        }
        return sb.quantityDropped(meta, fortune, random);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        for (int i = 0; i < this.subBlocksArray.length; i++) {
            if (subBlocksArray[i] != null) {
                par3List.add(new ItemStack(par1, 1, i));
            }
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 3;
        if (getSubBlock(meta) != null) {
            return new ItemStack(Item.getItemFromBlock(this), 1, getDistinctionMeta(meta));
        }

        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess par1World, int x, int y, int z) {
        return true;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates. Args: blockAccess, x, y, z, side
     */

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        // return true;
        Block block = world.getBlock(x, y, z);
        if (!this.isOpaqueCube() && block == this) {
            return true;
        }
        return super.shouldSideBeRendered(world, x, y, z, side);
        // return !this.isOpaqueCube() && block == this ? false : super.shouldSideBeRendered(world, x, y, z, side);

    }

    @Override
    public boolean renderAsNormalBlock() {
        return this.isOpaqueCube();
    }

    @Override
    public boolean isOpaqueCube() {
        return Blocks.leaves.isOpaqueCube();
    }

    @Override
    public String getUnlocalizedSubBlockName(int meta) {
        final SubBlock sb = this.getSubBlock(meta);
        if (sb != null) {
            return sb.getUnlocalizedName();
        }
        return this.getUnlocalizedName();
    }

    @Override
    public int getNumPossibleSubBlocks() {
        return 4;
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ,
        int meta) {
        final SubBlock sb = this.getSubBlock(meta);
        if (sb != null) {
            return sb.onBlockPlaced(worldIn, x, y, z, side, subX, subY, subZ, meta);
        }
        return super.onBlockPlaced(worldIn, x, y, z, side, subX, subY, subZ, meta);
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
    }

}
