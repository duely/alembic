package com.noobanidus.alembic.handlers;

import com.noobanidus.alembic.Alembic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

public class ThaumcraftListener {
    public ThaumcraftListener() {
        ThaumcraftListenerInternal internal = new ThaumcraftListenerInternal();
    }

    @ParametersAreNonnullByDefault
    public static class ThaumcraftListenerInternal implements ISelectiveResourceReloadListener {
        public ThaumcraftListenerInternal() {
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
            Alembic.RESEARCH_HANDLER.reload(null);

            MinecraftServer server = DimensionManager.getWorld(0).getMinecraftServer();
            if (server != null) {
                server.getPlayerList().reloadResources();
            }
        }
    }
}