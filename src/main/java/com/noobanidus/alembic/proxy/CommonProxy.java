package com.noobanidus.alembic.proxy;

import com.noobanidus.alembic.Alembic;
import com.noobanidus.alembic.AlembicConfig;
import com.noobanidus.alembic.commands.AlembicCommand;
import com.noobanidus.alembic.commands.ResearchCommand;
import com.noobanidus.alembic.events.AlembicEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.*;

public class CommonProxy implements ISidedProxy {
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(AlembicEvents.class);
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        Alembic.LOG.info("Load Complete.");
    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new ResearchCommand());
        event.registerServerCommand(new AlembicCommand());
    }

    public void serverStarted(FMLServerStartedEvent event) {
        if (!AlembicConfig.enable) return;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (server == null) {
            Alembic.LOG.info("Not doing anything with custom advancements as we appear to be on the client.");
            return;
        }

        Alembic.RESEARCH_HANDLER.reload(server);
    }
}
