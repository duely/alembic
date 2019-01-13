package com.noobanidus.alembic.events;

import com.noobanidus.alembic.Alembic;
import com.noobanidus.alembic.AlembicConfig;
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

        if (!player.world.isRemote && player.ticksExisted % (AlembicConfig.interval * 20) == 0) {
            Alembic.THAUMCRAFT_RESEARCH_TRIGGER.trigger((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public static void onResearch(ResearchEvent.Research event) {
        EntityPlayer player = event.getPlayer();
        if (!player.world.isRemote) {
            Alembic.THAUMCRAFT_RESEARCH_TRIGGER.trigger((EntityPlayerMP) player);
            //player.sendMessage(new TextComponentString("There was a research event and a trigger was fired! " + event.getResearchKey()));
        }
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        if (!AlembicConfig.enable) return;

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
                    Alembic.RESEARCH_HANDLER.reload(server);
                } else {
                    server.addScheduledTask(() -> Alembic.RESEARCH_HANDLER.reload(server));
                }
            }
        }
    }

    /*@SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        if (event.getAdvancement().getId().getNamespace().equals("alembic")) {
            event.getEntityPlayer().sendMessage(new TextComponentString("You got an achievement! " + event.getAdvancement().getId().toString()));
        }
    }*/
}
