package dev.tauri.jsg.core.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JSGCriterion extends SimpleCriterionTrigger<JSGCriterion.TriggerInstance> {

    protected static final List<JSGCriterion> INSTANCES = new ArrayList<>();

    private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
    ).apply(instance, TriggerInstance::new));

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
    public @NotNull Codec<TriggerInstance> codec() {
        return CODEC;
    }

    public @NotNull TriggerInstance createInstance() {
        return new TriggerInstance(Optional.empty());
    }

    public @NotNull Criterion<TriggerInstance> createCriterion() {
        return this.createCriterion(createInstance());
    }

    public ResourceLocation getId() {
        return id;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
    }

    /**
     * 1.21: the trigger_type registry is frozen after registry events, so triggers must be
     * registered from RegisterEvent (also required for datagen, where common setup never runs).
     * All JSGCriterion instances are created during mod construction, which precedes registry events.
     */
    public static void onRegisterTriggers(net.neoforged.neoforge.registries.RegisterEvent event) {
        event.register(net.minecraft.core.registries.Registries.TRIGGER_TYPE, helper -> {
            for (JSGCriterion a : INSTANCES) {
                helper.register(a.id, a);
            }
        });
    }
}
