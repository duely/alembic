package com.noobanidus.alembic;

import com.noobanidus.alembic.advancements.GenericTrigger;
import com.noobanidus.alembic.advancements.ResearchPredicate;
import com.noobanidus.alembic.commands.AlembicCommand;
import com.noobanidus.alembic.commands.ResearchCommand;
import com.noobanidus.alembic.events.AlembicEvents;
import com.noobanidus.alembic.handlers.ResearchHandler;
import com.noobanidus.alembic.handlers.ThaumcraftListener;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(AlembicEvents.class);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        LOG.info("Load Complete.");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new ResearchCommand());
        event.registerServerCommand(new AlembicCommand());
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        if (AlembicConfig.isDesabled()) return;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            Alembic.LOG.info("Not doing anything with custom advancements as we appear to be on the client.");
        }

        RESEARCH_HANDLER.reloadFull();

        if (server instanceof IntegratedServer) {
            TC_LISTENER = new ThaumcraftListener();
            // Listeners automatically trigger upon registering?
        }
    }

    @Config(modid = Alembic.MODID)
    public static class AlembicConfig {
        @Config.Comment("Specify number of seconds between checks for reearch")
        @Config.Name("Research Check Interval")
        @Config.RangeInt(min = 1)
        public static int interval = 9;

        @Config.Comment({"Disable automatic registration of research events", "If Triumph is also loaded, events are automatically disabled"})
        @Config.Name("Disable Events")
        public static boolean disable = false;

        public static boolean isDesabled () {
            if (Loader.isModLoaded("triump")) {
                return false;
            }

            return disable;
        }
    }
}
