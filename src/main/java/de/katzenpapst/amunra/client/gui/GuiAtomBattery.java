package de.katzenpapst.amunra.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.inventory.ContainerAtomBattery;
import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class GuiAtomBattery extends GuiContainerGC {

    private static final ResourceLocation solarGuiTexture = new ResourceLocation(
        AmunRa.ASSETPREFIX,
        "textures/gui/atomgenerator.png");

    private final TileEntityIsotopeGenerator generatorTile;

    private GuiButton buttonEnableSolar;
    private GuiElementInfoRegion electricInfoRegion = new GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 107,
        (this.height - this.ySize) / 2 + 101,
        56,
        9,
        new ArrayList<String>(),
        this.width,
        this.height,
        this);

    public GuiAtomBattery(InventoryPlayer par1InventoryPlayer, TileEntityIsotopeGenerator generator) {
        super(new ContainerAtomBattery(par1InventoryPlayer, generator));
        this.generatorTile = generator;
        this.ySize = 201;
        this.xSize = 176;
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        switch (par1GuiButton.id) {
            case 0:
                GalacticraftCore.packetPipeline.sendToServer(
                    new PacketSimple(
                        EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                        new Object[] { this.generatorTile.xCoord, this.generatorTile.yCoord, this.generatorTile.zCoord,
                            0 }));
                break;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        electricityDesc.add(
            EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1")
                + ((int) Math.floor(this.generatorTile.getEnergyStoredGC()) + " / "
                    + (int) Math.floor(this.generatorTile.getMaxEnergyStoredGC())));
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 96;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 24;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(
            new GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 151,
                (this.height - this.ySize) / 2 + 82,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this));
        // List<String> sunGenDesc = new ArrayList<String>();

        this.buttonList.add(
            this.buttonEnableSolar = new GuiButton(
                0,
                this.width / 2 - 36,
                this.height / 2 - 19,
                72,
                20,
                GCCoreUtil.translate("gui.button.enable.name")));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int offsetY = 35;
        this.buttonEnableSolar.enabled = this.generatorTile.disableCooldown == 0;
        this.buttonEnableSolar.displayString = !this.generatorTile.getDisabled(0)
            ? GCCoreUtil.translate("gui.button.disable.name")
            : GCCoreUtil.translate("gui.button.enable.name");
        String displayString = this.generatorTile.getInventoryName();
        this.fontRendererObj.drawString(
            displayString,
            this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2,
            7,
            4210752);

        displayString = GCCoreUtil.translate("gui.message.generating.name") + ": "
            + (this.generatorTile.generateWatts > 0
                ? EnergyDisplayHelper.getEnergyDisplayS(this.generatorTile.generateWatts) + "/t"
                : GCCoreUtil.translate("gui.status.notGenerating.name"));
        this.fontRendererObj.drawString(
            displayString,
            this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2,
            45 + 23 - 46 + offsetY,
            4210752);

        /* TODO maybe make the temperature a boost? the colder the better? */
        float boost = Math.round((this.generatorTile.getEnvironmentalEnergyBoost() - 1) * 1000) / 10.0F;
        displayString = GCCoreUtil.translate("gui.message.environment.name") + ": " + boost + "%";
        this.fontRendererObj.drawString(
            displayString,
            this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2,
            56 + 23 - 46 + offsetY,
            4210752);

        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 94, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(solarGuiTexture);
        final int xPos = (this.width - this.xSize) / 2;
        final int yPos = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);

        List<String> electricityDesc = new ArrayList<String>();
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.generatorTile.getEnergyStoredGC(),
            this.generatorTile.getMaxEnergyStoredGC(),
            electricityDesc);
        // electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        // electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1") + ((int)
        // Math.floor(this.solarPanel.getEnergyStoredGC()) + " / " + (int)
        // Math.floor(this.solarPanel.getMaxEnergyStoredGC())));
        this.electricInfoRegion.tooltipStrings = electricityDesc;

        if (this.generatorTile.getEnergyStoredGC() > 0) {
            this.drawTexturedModalRect(xPos + 83, yPos + 24, 176, 0, 11, 10);
        }

        if (!this.generatorTile.getDisabled(0)) {
            this.drawTexturedModalRect(xPos + 46, yPos + 19, 176, 10, 20, 20);
        }

        // this.drawTexturedModalRect(var5 + 97, var6 + 25, 187, 0, 54, 7);
        this.drawTexturedModalRect(
            xPos + 97,
            yPos + 25,
            187,
            0,
            Math.min(this.generatorTile.getScaledElecticalLevel(54), 54),
            7);
    }

}
