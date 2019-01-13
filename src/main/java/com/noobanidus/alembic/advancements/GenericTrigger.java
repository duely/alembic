package com.noobanidus.alembic.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.*;

public class GenericTrigger implements ICriterionTrigger<GenericTrigger.Instance> {
    private final ResourceLocation id;
    private final Map<PlayerAdvancements, GenericTrigger.Listeners> listeners = new HashMap<>();
    private final IGenericPlayerPredicate predicate;

    public GenericTrigger(String id, IGenericPlayerPredicate predicate) {
        this(new ResourceLocation(id), predicate);
    }

    public GenericTrigger(ResourceLocation id, IGenericPlayerPredicate predicate) { //}, IGenericPlayerPredicate basePredicate) {
        this.id = id;
        this.predicate = predicate;
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void addListener(@Nonnull PlayerAdvancements advancementsIn, @Nonnull ICriterionTrigger.Listener<GenericTrigger.Instance> listener) {
        GenericTrigger.Listeners list = listeners.get(advancementsIn);

        if (list == null) {
            list = new GenericTrigger.Listeners(advancementsIn);
            listeners.put(advancementsIn, list);
        }

        list.add(listener);
    }

    @Override
    public void removeListener(@Nonnull PlayerAdvancements advancementsIn, @Nonnull ICriterionTrigger.Listener<GenericTrigger.Instance> listener) {
        GenericTrigger.Listeners list = listeners.get(advancementsIn);

        if (list != null) {
            list.remove(listener);

            if (list.isEmpty()) {
                listeners.remove(advancementsIn);
            }
        }
    }

    @Override
    public void removeAllListeners(@Nonnull PlayerAdvancements advancementsIn) {
        listeners.remove(advancementsIn);
    }

    @Override
    @Nonnull
    public GenericTrigger.Instance deserializeInstance(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
        return new GenericTrigger.Instance(getId(), predicate.deserialize(json));
    }

    public void trigger(EntityPlayerMP player) {
        GenericTrigger.Listeners list = listeners.get(player.getAdvancements());

        if (list != null) {
            list.trigger(player);
        }
    }

    public static class Instance extends AbstractCriterionInstance {
        IGenericPlayerPredicate predicate;

        public Instance(ResourceLocation location, IGenericPlayerPredicate predicate) {
            super(location);

            this.predicate = predicate;
        }

        boolean test(EntityPlayerMP player) {
            return predicate.test(player);
        }
    }


    static class Listeners {
        PlayerAdvancements advancements;
        Set<ICriterionTrigger.Listener<GenericTrigger.Instance>> listeners = new HashSet<>();

        Listeners(PlayerAdvancements advancementsIn) {
            this.advancements = advancementsIn;
        }

        public boolean isEmpty() {
            return listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<GenericTrigger.Instance> listener) {
            listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<GenericTrigger.Instance> listener) {
            listeners.remove(listener);
        }

        void trigger(EntityPlayerMP player) {
            List<ICriterionTrigger.Listener<GenericTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<GenericTrigger.Instance> listener : listeners) {
                if (listener.getCriterionInstance().test(player)) {
                    list.add(listener);
                }
            }

            if (list.size() != 0) {
                for (ICriterionTrigger.Listener<GenericTrigger.Instance> listener : list) {
                    listener.grantCriterion(advancements);
                }
            }
        }
    }
}

