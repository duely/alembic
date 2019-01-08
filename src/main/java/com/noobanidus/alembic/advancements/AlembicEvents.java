package com.noobanidus.alembic.advancements;

import com.noobanidus.alembic.Alembic;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.research.ResearchEvent;

public class AlembicEvents {
    public static GenericTrigger THAUMCRAFT_RESEARCH_TRIGGER = CriteriaTriggers.register(new GenericTrigger(new ResourceLocation(Alembic.MODID, "research"), ResearchPredicate.ANY));

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        World world = player.world;

        if (!world.isRemote && player.ticksExisted % 160 == 0) {
            THAUMCRAFT_RESEARCH_TRIGGER.trigger((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public static void onResearch(ResearchEvent.Research event) {
        EntityPlayer player = event.getPlayer();
        if (!player.world.isRemote) {
            THAUMCRAFT_RESEARCH_TRIGGER.trigger((EntityPlayerMP) player);
        }
    }
}
