package com.noobanidus.alembic.events;

import com.noobanidus.alembic.Alembic;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandReload;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.research.ResearchEvent;

public class AlembicEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        if (!player.world.isRemote && player.ticksExisted % (Alembic.AlembicConfig.interval * 20) == 0) {
            Alembic.THAUMCRAFT_RESEARCH_TRIGGER.trigger((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public static void onResearch(ResearchEvent.Research event) {
        EntityPlayer player = event.getPlayer();
        if (!player.world.isRemote) {
            Alembic.THAUMCRAFT_RESEARCH_TRIGGER.trigger((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        if (Alembic.AlembicConfig.isDesabled()) return;

        ICommand command = event.getCommand();

        if (command instanceof CommandReload) {
            String[] args = event.getParameters();
            ICommandSender sender = event.getSender();
            MinecraftServer server = sender.getServer();
            if (args.length == 0 && server != null) {
                event.setCanceled(true);

                try {
                    command.execute(server, sender, args);
                } catch (CommandException e) {
                    Alembic.LOG.error("Error re-routing reload command:");
                    e.printStackTrace();
                    return;
                }

                if (server.isCallingFromMinecraftThread()) {
                    Alembic.RESEARCH_HANDLER.installAdvancements();
                } else {
                    server.addScheduledTask(Alembic.RESEARCH_HANDLER::installAdvancements);
                }
            }
        }
    }
}
