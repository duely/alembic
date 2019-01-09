package com.noobanidus.alembic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.research.ResearchEvent;

public class AlembicEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        if (!player.world.isRemote && player.ticksExisted % 160 == 0) {
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
}
