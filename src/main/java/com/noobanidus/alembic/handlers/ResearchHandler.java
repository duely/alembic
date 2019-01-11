package com.noobanidus.alembic.handlers;

import com.noobanidus.alembic.Alembic;
import com.noobanidus.alembic.advancements.GenericTrigger;
import com.noobanidus.alembic.advancements.ResearchPredicate;
import net.minecraft.advancements.*;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.research.ResearchCategories;

import java.util.HashMap;
import java.util.Map;

public class ResearchHandler {
    private AdvancementList ADVANCEMENT_LIST = AdvancementManager.ADVANCEMENT_LIST;
    private ResourceLocation ROOT_RESOURCE = new ResourceLocation(Alembic.MODID, "root");
    private Advancement ROOT = null;
    private Map<ResourceLocation, String> RESEARCH = new HashMap<>();
    private Map<ResourceLocation, Advancement> ADVANCEMENTS = new HashMap<>();

    public void reloadFull() {
        buildResearch();
        buildAdvancements();
        reload();
    }

    public void reload() {
        if (!validateAdvancements()) installAdvancements();
    }

    public Advancement getRoot() {
        if (ROOT == null) {
            ROOT = ADVANCEMENT_LIST.getAdvancement(ROOT_RESOURCE);
        }

        return ROOT;
    }

    public Map<ResourceLocation, String> getResearch() {
        return RESEARCH;
    }

    private void buildResearch() {
        RESEARCH.clear();

        ResearchCategories.researchCategories.forEach((name, cat) -> {
            cat.research.forEach((rname, entry) -> {
                for (int i = 0; i < entry.getStages().length; i++) {
                    String k = String.format("%s@%d", entry.getKey(), i + 1);
                    ResourceLocation r = new ResourceLocation(Alembic.MODID, String.format("%s/%s", name.toLowerCase(), k.toLowerCase()));
                    RESEARCH.put(r, k);
                }
            });
        });
    }

    private void buildAdvancements() {
        ADVANCEMENTS.clear();

        Advancement root = getRoot();

        if (root == null) {
            Alembic.LOG.error("Unable to build advancements at this stage: invalid root advancement node");
            return;
        }

        RESEARCH.forEach((rl, string) -> {
            Advancement test = ADVANCEMENT_LIST.getAdvancement(rl);

            if (test == null) {
                GenericTrigger.Instance instance = new GenericTrigger.Instance(Alembic.THAUMCRAFT_RESEARCH_TRIGGER.getId(), new ResearchPredicate(string));
                Map<String, Criterion> criteria = new HashMap<>();
                criteria.put("thaumcraft", new Criterion(instance));
                Advancement adv = new Advancement(rl, root, root.getDisplay(), AdvancementRewards.EMPTY, criteria, new String[][]{{"thaumcraft"}});
                ADVANCEMENTS.put(rl, adv);
            } else {
                ADVANCEMENTS.put(rl, test);
            }
        });
    }

    private boolean validateAdvancements() {
        return ADVANCEMENTS.keySet().stream().allMatch((k) -> ADVANCEMENT_LIST.getAdvancement(k) != null);
    }

    public long invalidAdvancementCount() {
        return ADVANCEMENTS.keySet().stream().filter((k) -> ADVANCEMENT_LIST.getAdvancement(k) != null).count();
    }

    public long advancementCount() {
        return ADVANCEMENTS.size();
    }

    public void installAdvancements() {
        int i = 0;

        for (Map.Entry<ResourceLocation, Advancement> entry : ADVANCEMENTS.entrySet()) {
            ResourceLocation rl = entry.getKey();
            Advancement adv = entry.getValue();

            if (ADVANCEMENT_LIST.getAdvancement(rl) == null) {
                ADVANCEMENT_LIST.advancements.put(rl, adv);
                ADVANCEMENT_LIST.nonRoots.add(adv);
                if (ADVANCEMENT_LIST.listener != null) {
                    ADVANCEMENT_LIST.listener.nonRootAdvancementAdded(adv);
                }
                i++;
            }
        }

        Alembic.LOG.info(String.format("Successfully loaded %d research events.", i));
    }
}
