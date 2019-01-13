package com.noobanidus.alembic.proxy;

import com.noobanidus.alembic.Alembic;
import com.noobanidus.alembic.AlembicConfig;
import com.noobanidus.alembic.handlers.ThaumcraftListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    @Override
    public void serverStarted(FMLServerStartedEvent event) {
        if (!AlembicConfig.enable) return;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (server == null) {
            Alembic.LOG.info("Not doing anything with custom advancements as we appear to be on the client.");
            return;
        }

        if (server instanceof IntegratedServer) {
            Alembic.TC_LISTENER = new ThaumcraftListener();
            // Listeners automatically trigger upon registering?
        } else {
            Alembic.RESEARCH_HANDLER.reload(server);
        }
    }
}
