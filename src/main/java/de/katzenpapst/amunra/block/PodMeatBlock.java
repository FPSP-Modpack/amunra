package de.katzenpapst.amunra.block;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PodMeatBlock extends SubBlock {

    public PodMeatBlock(String name, String texture) {
        super(name, texture, "axe", 1);
    }

    @Override
    public boolean dropsSelf() {
        return false;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(ARBlocks.blockPodSapling.getBlock());
    }

    @Override
    public int damageDropped(int meta) {
        return ARBlocks.blockPodSapling.getMetadata();
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates. Args: blockAccess, x, y, z, side
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return super.shouldSideBeRendered(worldIn, x, y, z, 1 - side);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random random) {
        return random.nextInt(100) > 90 ? 1 : 0;
    }

    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return random.nextInt(100) > (90 - 10 * fortune) ? 1 : 0;
    }

    /**
     * Metadata and fortune sensitive version, this replaces the old (int meta, Random rand)
     * version in 1.1.
     *
     * @param meta    Blocks Metadata
     * @param fortune Current item fortune level
     * @param random  Random number generator
     * @return The number of items to drop
     */
    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        /**
         * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
         */
        return quantityDroppedWithBonus(fortune, random);
    }

}
