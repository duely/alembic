package com.noobanidus.alembic.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ResearchPredicate implements IGenericPlayerPredicate {
    public static ResearchPredicate ANY = new ResearchPredicate(new ArrayList<>());
    private List<String> requirements;

    public ResearchPredicate(List<String> requirements) {
        this.requirements = requirements;
    }

    @Override
    public boolean test(EntityPlayerMP player) {
        return ThaumcraftCapabilities.knowsResearchStrict(player, requirements.toArray(new String[0]));
    }

    @Override
    public ResearchPredicate deserialize(@Nullable JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            JsonObject jsonobject = element.getAsJsonObject();
            List<String> researchTypes = Lists.newArrayList();

            if (jsonobject.has("research")) {
                for (JsonElement elem : JsonUtils.getJsonArray(jsonobject, "research")) {
                    String key = elem.getAsString();
                    researchTypes.add(key);
                }
            }

            return new ResearchPredicate(researchTypes);
        } else {
            return ANY;
        }
    }
}
