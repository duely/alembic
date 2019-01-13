package com.noobanidus.alembic;

import com.noobanidus.alembic.advancements.GenericTrigger;
import com.noobanidus.alembic.advancements.ResearchPredicate;
import com.noobanidus.alembic.handlers.ResearchHandler;
import com.noobanidus.alembic.handlers.ThaumcraftListener;
import com.noobanidus.alembic.proxy.ISidedProxy;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = Alembic.MODID, name = Alembic.MODNAME, version = Alembic.VERSION, dependencies = Alembic.DEPENDS)
@SuppressWarnings("WeakerAccess")
public class Alembic {
    public static final String MODID = "alembic";
    public static final ResourceLocation RESEARCH_ID = new ResourceLocation(Alembic.MODID, "research");
    public static final GenericTrigger THAUMCRAFT_RESEARCH_TRIGGER = CriteriaTriggers.register(new GenericTrigger(RESEARCH_ID, ResearchPredicate.ANY));
    public static final String MODNAME = "Alembic";
    public static final String VERSION = "GRADLE:VERSION";
    public static final String DEPENDS = "required-after:thaumcraft;";
    @SuppressWarnings("unused")
    public static final String KEY = "ca23084fc26ce53879eea4b7afb0a8d9da9744d7";
    public final static Logger LOG = LogManager.getLogger(MODID);
    public static ThaumcraftListener TC_LISTENER = null;
    @SuppressWarnings("unused")
    @Mod.Instance(Alembic.MODID)
    public static Alembic instance;
    public static ResearchHandler RESEARCH_HANDLER = new ResearchHandler();
    @SidedProxy(clientSide = "com.noobanidus.alembic.proxy.ClientProxy", modId = MODID, serverSide = "com.noobanidus.alembic.proxy.CommonProxy")
    public static ISidedProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        proxy.serverStarted(event);
    }

}
