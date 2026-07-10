package dev.tauri.jsg.core.common.advancement;

import com.google.gson.JsonObject;
import dev.tauri.jsg.core.JSGCore;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class JSGCriterion extends SimpleCriterionTrigger<JSGCriterion.TriggerInstance> {

    protected static final List<JSGCriterion> INSTANCES = new ArrayList<>();

    public final ResourceLocation id;

    public JSGCriterion(ResourceLocation id) {
        this.id = id;
        INSTANCES.add(this);
    }

    public void trigger(ServerPlayer player) {
        if (player == null) return;
        this.trigger(player, (instance) -> true);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected @NotNull TriggerInstance createInstance(JsonObject pJson, ContextAwarePredicate pPredicate, DeserializationContext pDeserializationContext) {
        return new TriggerInstance(id, pPredicate);
    }

    public @NotNull TriggerInstance createInstance() {
        return new TriggerInstance(id, ContextAwarePredicate.ANY);
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance(ResourceLocation pCriterion, ContextAwarePredicate pPlayer) {
            super(pCriterion, pPlayer);
        }
    }

    public static void registerInternally() {
        for (JSGCriterion a : INSTANCES) {
            CriteriaTriggers.register(a);
        }
        JSGCore.logger.info("Successfully registered Advancement Triggers!");
    }
}