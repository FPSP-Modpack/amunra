package de.katzenpapst.amunra.command;

import de.katzenpapst.amunra.entity.spaceship.EntityShuttle;
import micdoodle8.mods.galacticraft.api.entity.IRocketType;
import micdoodle8.mods.galacticraft.core.GCItems;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;

public class CommandShuttleTeleport extends CommandBase {

    public CommandShuttleTeleport() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 1;
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/" + this.getCommandName() + " [<player>]";
    }

    @Override
    public String getCommandName()
    {
        return "shuttle_tp";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) throws CommandException
    {
        EntityPlayerMP playerBase = null;
        Entity sender = icommandsender.getCommandSenderEntity();

        if (astring.length < 2)
        {
            try
            {
                if (astring.length == 1)
                {
                    playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(astring[0], true);
                }
                else
                {
                    if(sender != null && sender instanceof EntityPlayerMP) {
                        playerBase = (EntityPlayerMP) sender;
                    }
                }

                if (playerBase != null)
                {
                    MinecraftServer server = MinecraftServer.getServer();
                    WorldServer worldserver = server.worldServerForDimension(server.worldServers[0].provider.getDimensionId());
                    BlockPos chunkcoordinates = worldserver.getSpawnPoint();
                    GCPlayerStats stats = GCPlayerStats.get(playerBase);



                    stats.setRocketStacks(new ItemStack[2]);
                    stats.setRocketType(IRocketType.EnumRocketType.DEFAULT.ordinal());
                    stats.setRocketItem(GCItems.rocketTier1); // or maybe use the shuttle here?
                    stats.setFuelLevel(1000);
                    stats.setCoordsTeleportedFromX(chunkcoordinates.getX());
                    stats.setCoordsTeleportedFromZ(chunkcoordinates.getZ());

                    try
                    {
                        EntityShuttle.toCelestialSelection(playerBase, stats, Integer.MAX_VALUE, false);
                        // WorldUtil.toCelestialSelection(playerBase, stats, Integer.MAX_VALUE);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw e;
                    }

                    // VersionUtil.notifyAdmins(icommandsender, this, "commands.dimensionteleport", new Object[] { String.valueOf(EnumColor.GREY + "[" + playerBase.getCommandSenderName()), "]" });
                }
                else
                {
                    throw new Exception("Could not find player with name: " + astring[0]);
                }
            }
            catch (final Exception var6)
            {
                throw new CommandException(var6.getMessage(), new Object[0]);
            }
        }
        else
        {
            throw new WrongUsageException(GCCoreUtil.translateWithFormat("commands.dimensiontp.tooMany", this.getCommandUsage(icommandsender)), new Object[0]);
        }
    }

}


