package com.noobanidus.alembic.handlers;

import com.noobanidus.alembic.Alembic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class ThaumcraftListener implements ISelectiveResourceReloadListener {
    private final Minecraft mc = Minecraft.getMinecraft();

    public ThaumcraftListener () {
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(this);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        Alembic.RESEARCH_HANDLER.reload();
    }
}
