package de.katzenpapst.amunra.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.tile.TileEntityShuttleDockFake;
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti;

public class FakeBlock extends SubBlock implements IPartialSealableBlock, IMassiveBlock {

    public FakeBlock(String name, String texture) {
        super(name, texture);
        this.setHardness(1.0F);
        this.setStepSound(Block.soundTypeMetal);
        this.setResistance(1000000000000000.0F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canDropFromExplosion(Explosion par1Explosion) {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        if (worldIn.getTileEntity(x, y, z) instanceof TileEntityMulti tileEntity) {
            final BlockVec3 mainBlockPosition = tileEntity.mainBlockPosition;
            if (mainBlockPosition != null) {
                return mainBlockPosition.getBlock(worldIn)
                    .getBlockHardness(worldIn, x, y, z);
            }
        }

        return this.blockHardness;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        return null;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityMulti) {
            ((TileEntityMulti) tileEntity).onBlockRemoval();
        }

        super.breakBlock(world, x, y, z, par5, par6);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (worldIn.getTileEntity(x, y, z) instanceof TileEntityMulti tileEntity) {
            return tileEntity.onBlockActivated(worldIn, x, y, z, player);
        }
        return super.onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ);
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World var1, int meta) {
        return new TileEntityShuttleDockFake();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityMulti) {
            BlockVec3 mainBlockPosition = ((TileEntityMulti) tileEntity).mainBlockPosition;

            if (mainBlockPosition != null) {
                Block mainBlockID = world.getBlock(mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z);

                if (Blocks.air != mainBlockID) {
                    return mainBlockID
                        .getPickBlock(target, world, mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z);
                }
            }
        }

        return null;
    }

    @Override
    public boolean isSealed(World world, int x, int y, int z, ForgeDirection direction) {
        return true;
    }

    @Override
    public float getMass(World w, int x, int y, int z, int meta) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        TileEntity tileEntity = worldObj.getTileEntity(target.blockX, target.blockY, target.blockZ);

        if (tileEntity instanceof TileEntityMulti) {
            BlockVec3 mainBlockPosition = ((TileEntityMulti) tileEntity).mainBlockPosition;

            if (mainBlockPosition != null) {
                effectRenderer
                    .addBlockHitEffects(mainBlockPosition.x, mainBlockPosition.y, mainBlockPosition.z, target);
            }
        }

        return super.addHitEffects(worldObj, target, effectRenderer);
    }

}
