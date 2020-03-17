package com.terraforged.feature.util;

import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface FeatureVisitor {

    void visit(Feature<?> feature, FeatureConfig config);

    void visit(Decorator<?> decorator, DecoratorConfig config);

    default void visitConfigured(ConfiguredFeature<?, ?> feature) {
        if (feature.config instanceof DecoratedFeatureConfig) {
            visitDecorated((DecoratedFeatureConfig) feature.config);
            return;
        }

        // note SingleRandomFeature & SingleRandomFeatureConfig names a mixed up
        if (feature.config instanceof SimpleRandomFeatureConfig) {
            visitSingle((SimpleRandomFeatureConfig) feature.config);
            return;
        }

        if (feature.config instanceof RandomBooleanFeatureConfig) {
            visitTwoChoice((RandomBooleanFeatureConfig) feature.config);
            return;
        }

        if (feature.config instanceof RandomFeatureConfig) {
            visitMulti((RandomFeatureConfig) feature.config);
            return;
        }

        if (feature.config instanceof RandomRandomFeatureConfig) {
            visitMultiChance((RandomRandomFeatureConfig) feature.config);
            return;
        }

        visit(feature.feature, feature.config);
    }

    default void visitDecorated(DecoratedFeatureConfig config) {
        visitConfigured(config.feature);

        visit(config.decorator.decorator, config.decorator.config);
    }

    default void visitSingle(SimpleRandomFeatureConfig config) {
        for (ConfiguredFeature<?, ?> feature : config.features) {
            visitConfigured(feature);
        }
    }

    default void visitTwoChoice(RandomBooleanFeatureConfig config) {
        visitConfigured(config.featureTrue);
        visitConfigured(config.featureFalse);
    }

    default void visitMulti(RandomFeatureConfig config) {
        for (RandomFeatureEntry<?> feature : config.features) {
            visitConfigured(feature.feature);
        }
    }

    default void visitMultiChance(RandomRandomFeatureConfig config) {
        for (ConfiguredFeature<?, ?> feature : config.features) {
            visitConfigured(feature);
        }
    }

    interface FeatureV extends FeatureVisitor {

        default void visit(Decorator<?> decorator, DecoratorConfig config) {

        }
    }

    interface DecoratorV extends FeatureVisitor {

        default void visit(Feature<?> feature, FeatureConfig config) {

        }
    }

    static Predicate<ConfiguredFeature<?, ?>> featureFilter(Predicate<Feature<?>> predicate) {
        return feature -> {
            AtomicBoolean result = new AtomicBoolean();
            FeatureVisitor.feature(f -> {
                if (predicate.test(f)) {
                    result.set(true);
                }
            }).visitConfigured(feature);
            return result.get();
        };
    }

    static FeatureVisitor feature(Consumer<Feature<?>> consumer) {
        return create((f, c) -> consumer.accept(f), (d, c) -> {});
    }

    static FeatureVisitor featureConfig(Consumer<FeatureConfig> consumer) {
        return create((f, c) -> consumer.accept(c), (d, c) -> {});
    }

    static <T extends Feature<?>> FeatureVisitor feature(Class<T> type, Consumer<T> consumer) {
        return feature(consumerOf(type::isInstance, type::cast, consumer));
    }

    static <T extends FeatureConfig> FeatureVisitor featureConfig(Class<T> type, Consumer<T> consumer) {
        return featureConfig(consumerOf(type::isInstance, type::cast, consumer));
    }

    static FeatureVisitor feature(BiConsumer<Feature<?>, FeatureConfig> consumer) {
        return create(consumer, (d, c) -> {});
    }

    static FeatureVisitor decorator(Consumer<Decorator<?>> consumer) {
        return create((f, c) -> {}, (d, c) -> consumer.accept(d));
    }

    static FeatureVisitor decoratorConfig(Consumer<DecoratorConfig> consumer) {
        return create((f, c) -> {}, (d, c) -> consumer.accept(c));
    }

    static <T extends Decorator<?>> FeatureVisitor decorator(Class<T> type, Consumer<T> consumer) {
        return decorator(consumerOf(type::isInstance, type::cast, consumer));
    }

    static <T extends DecoratorConfig> FeatureVisitor decoratorConfig(Class<T> type, Consumer<T> consumer) {
        return decoratorConfig(consumerOf(type::isInstance, type::cast, consumer));
    }

    static FeatureVisitor decorator(BiConsumer<Decorator<?>, DecoratorConfig> consumer) {
        return create((f, c) -> {}, consumer);
    }

    static <In, Out> Consumer<In> consumerOf(Predicate<In> predicate, Function<In, Out> mapper, Consumer<Out> consumer) {
        return in -> {
            if (predicate.test(in)) {
                Out out = mapper.apply(in);
                consumer.accept(out);
            }
        };
    }

    static FeatureVisitor create(BiConsumer<Feature<?>, FeatureConfig> features, BiConsumer<Decorator<?>, DecoratorConfig> decorators) {
        return new FeatureVisitor() {
            @Override
            public void visit(Feature<?> feature, FeatureConfig config) {
                features.accept(feature, config);
            }

            @Override
            public void visit(Decorator<?> decorator, DecoratorConfig config) {
                decorators.accept(decorator, config);
            }
        };
    }
}
