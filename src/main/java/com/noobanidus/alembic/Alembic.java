package com.noobanidus.alembic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.noobanidus.alembic.advancements.GenericTrigger;
import com.noobanidus.alembic.advancements.ResearchPredicate;
import com.noobanidus.alembic.commands.ResearchCommand;
import com.noobanidus.alembic.events.AlembicEvents;
import net.minecraft.advancements.*;
import net.minecraft.command.CommandHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
@Mod(modid = Alembic.MODID, name = Alembic.MODNAME, version = Alembic.VERSION, dependencies = Alembic.DEPENDS)
@SuppressWarnings("WeakerAccess")
public class Alembic {
    public static final GenericTrigger THAUMCRAFT_RESEARCH_TRIGGER = CriteriaTriggers.register(new GenericTrigger(new ResourceLocation(Alembic.MODID, "research"), ResearchPredicate.ANY));

    public static final String MODID = "alembic";
    public static final String MODNAME = "Alembic";
    public static final String VERSION = "GRADLE:VERSION";
    public static final String DEPENDS = "required-after:thaumcraft;";

    @SuppressWarnings("unused")
    public static final String KEY = "ca23084fc26ce53879eea4b7afb0a8d9da9744d7";

    public final static Logger LOG = LogManager.getLogger(MODID);

    @SuppressWarnings("unused")
    @Mod.Instance(Alembic.MODID)
    public static Alembic instance;

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
        if (event.getSide() == Side.CLIENT) {
            ClientCommandHandler.instance.registerCommand(new ResearchCommand());
        }
        LOG.info("Load Complete.");
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        Map<ResourceLocation, List<String>> custom = AlembicConfig.getCustomResearches();
        if (custom.size() == 0) return;

        if (FMLCommonHandler.instance().getMinecraftServerInstance() == null) {
            Alembic.LOG.info("Not doing anything with custom advancements as we appear to be on the client.");
            return;
        }

        AdvancementList list = AdvancementManager.ADVANCEMENT_LIST;

        Advancement root = list.getAdvancement(new ResourceLocation(Alembic.MODID, "root"));

        custom.forEach((rl, string) -> {
            // display needs to be the same
            GenericTrigger.Instance instance = new GenericTrigger.Instance(THAUMCRAFT_RESEARCH_TRIGGER.getId(), new ResearchPredicate(string));
            Map<String, Criterion> criteria = Maps.newHashMap();
            criteria.put("thaumcraft", new Criterion(instance));
            Advancement m = new Advancement(rl, root, root.getDisplay(), AdvancementRewards.EMPTY, criteria, new String[][]{{"thaumcraft"}});

            list.advancements.put(rl, m);
            list.nonRoots.add(m);
            if (list.listener != null) {
                list.listener.nonRootAdvancementAdded(m);
            }
        });
    }

    @Config(modid=Alembic.MODID)
    public static class AlembicConfig {
        @Config.RequiresWorldRestart
        @Config.Comment({"Syntax example: \"!Pech&&!Wisp,PechAndWisp\"", "Creates a custom advancement with the name: alembic:custom/pechandwisp", "This advancement will trigger when both scan researches are complete."})
        @Config.Name("Custom Research")
        public static String[] custom = new String[]{};

        @SuppressWarnings("unhandled")
        public static Map<ResourceLocation, List<String>> getCustomResearches () {
            Map<ResourceLocation, List<String>> result = Maps.newHashMap();

            String[] temp;

            for (String s : custom) {
                if (s.isEmpty()) continue;

                String name;
                String research;

                if (s.contains(",")) {
                    temp = s.split(",");
                    research = temp[0];
                    name = temp[1];
                } else {
                    Alembic.LOG.error(String.format("Invalid custom research advancement: |%s|. Research requires a name!", s));
                    continue;
                }

                ResourceLocation rl = new ResourceLocation(Alembic.MODID, String.format("custom/%s", name.trim()));

                if (result.containsKey(rl)) {
                    Alembic.LOG.error(String.format("Invalid name |%s| for advancement |%s|: name |%s| already exists! Skipping.", name.trim(), research.trim(), name.trim()));
                } else {
                    result.put(rl, Lists.newArrayList(research.trim()));
                }
            }

            return result;
        }
    }
}
