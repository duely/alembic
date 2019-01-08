package com.noobanidus.alembic;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber
@Mod(modid = Alembic.MODID, name = Alembic.MODNAME, version = Alembic.VERSION, dependencies = Alembic.DEPENDS)
@SuppressWarnings("WeakerAccess")
public class Alembic {
    public static final String MODID = "alembic";
    public static final String MODNAME = "Alembic";
    public static final String VERSION = "GRADLE:VERSION";
    public static final String DEPENDS = "require-after:thaumcraft;";

    @SuppressWarnings("unused")
    public static final String KEY = "ca23084fc26ce53879eea4b7afb0a8d9da9744d7";

    public final static Logger LOG = LogManager.getLogger(MODID);

    @SuppressWarnings("unused")
    @Mod.Instance(Alembic.MODID)
    public static Alembic instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
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
}
