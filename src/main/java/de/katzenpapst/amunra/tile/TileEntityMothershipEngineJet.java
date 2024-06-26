package de.katzenpapst.amunra.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelDisplay;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelDisplayFluid;
import de.katzenpapst.amunra.mothership.fueldisplay.MothershipFuelRequirements;
import de.katzenpapst.amunra.proxy.ARSidedProxy.ParticleType;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

/**
 * This is supposed to be used for any jet blocks
 * 
 * @author katzenpapst
 *
 */
public class TileEntityMothershipEngineJet extends TileEntityMothershipEngineAbstract {

    // public static final int MAX_LENGTH = 10;
    // protected PositionedSoundRecord leSound;

    // protected final MothershipFuel fuelType;
    protected MothershipFuelDisplay fuelType = null;
    public static Fluid jetFuel;

    public TileEntityMothershipEngineJet() {
        this.boosterBlock = ARBlocks.blockMsEngineRocketBooster;
        this.containingItems = new ItemStack[1];

        this.fuel = jetFuel;
        fuelType = new MothershipFuelDisplayFluid(this.fuel);
    }

    @Override
    public boolean shouldUseEnergy() {
        return false;
    }

    @Override
    public void beginTransit(long duration) {
        MothershipFuelRequirements reqs = this.getFuelRequirements(duration);
        int fuelReq = reqs.get(fuelType);
        this.fuelTank.drain(fuelReq, true);
        super.beginTransit(duration);

    }

    @Override
    protected boolean isItemFuel(ItemStack itemstack) {

        FluidStack containedFluid = null;
        if (itemstack.getItem() instanceof IFluidContainerItem) {
            containedFluid = ((IFluidContainerItem) itemstack.getItem()).getFluid(itemstack);
        }
        if (containedFluid == null) {
            containedFluid = FluidContainerRegistry.getFluidForFilledItem(itemstack);
        }
        if (containedFluid != null) {
            return this.fuel == containedFluid.getFluid();
        }
        return false;
    }

    /**
     * Calculates tank capacity based on the boosters
     * 
     * @return
     */
    @Override
    protected int getTankCapacity() {
        return 10000 * this.numBoosters;
    }

    @Override
    protected void startSound() {
        super.startSound();
        AmunRa.proxy.playTileEntitySound(this, new ResourceLocation(AmunRa.TEXTUREPREFIX + "mothership.engine.rocket"));
    }

    @Override
    protected void spawnParticles() {
        Vector3 particleStart = getExhaustPosition(1);
        Vector3 particleDirection = getExhaustDirection().scale(5);

        AmunRa.proxy
            .spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy
            .spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy
            .spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);
        AmunRa.proxy
            .spawnParticles(ParticleType.PT_MOTHERSHIP_JET_FLAME, this.worldObj, particleStart, particleDirection);

    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (this.fuel == fluid) {
            return super.canFill(from, fluid);
        }
        return false;
    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.mothershipEngineRocket.name");
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
        if (slotID == 0 && itemstack != null) {
            return this.isItemFuel(itemstack);
        }
        /*
         * FluidStack containedFluid = FluidContainerRegistry.getFluidForFilledItem(itemstack);
         * if(containedFluid.getFluid() == fuel) {
         * return true;
         * }
         */
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { 0 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side) {
        return this.isItemValidForSlot(slotID, itemstack);
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        return slotID == 0;
    }

    @Override
    public double getThrust() {
        return this.getNumBoosters() * 2000000.0D;
    }

    /**
     * This should return how much fuel units are consumed per AU travelled, in millibuckets
     * 
     * @return
     */
    public float getFuelUsagePerTick() {
        return 2.0F;
    }

    @Override
    public MothershipFuelRequirements getFuelRequirements(long duration) {
        int totalFuelNeed = (int) Math.ceil(this.getFuelUsagePerTick() * duration * AmunRa.config.mothershipFuelFactor);

        MothershipFuelRequirements result = new MothershipFuelRequirements();

        result.add(fuelType, totalFuelNeed);

        return result;
    }

    @Override
    public boolean canRunForDuration(long duration) {
        MothershipFuelRequirements reqs = getFuelRequirements(duration);

        return reqs.get(fuelType) <= fuelTank.getFluidAmount();
    }

}
