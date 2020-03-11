package com.terraforged.feature.util;

import com.mojang.datafixers.types.JsonOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;

import java.util.ArrayList;
import java.util.List;

public class FeatureDebugger {

    public static List<String> getErrors(ConfiguredFeature<?, ?> feature) {
        List<String> errors = new ArrayList<>();
        checkConfiguredFeature(feature, errors);
        return errors;
    }

    private static void checkConfiguredFeature(ConfiguredFeature<?, ?> feature, List<String> errors) {
        if (feature.config instanceof DecoratedFeatureConfig) {
            decorated((DecoratedFeatureConfig) feature.config, errors);
            return;
        }

        if (feature.config instanceof SimpleRandomFeatureConfig) {
            single((SimpleRandomFeatureConfig) feature.config, errors);
            return;
        }

        if (feature.config instanceof RandomBooleanFeatureConfig) {
            twoChoice((RandomBooleanFeatureConfig) feature.config, errors);
            return;
        }

        if (feature.config instanceof RandomFeatureConfig) {
            multi((RandomFeatureConfig) feature.config, errors);
            return;
        }

        if (feature.config instanceof RandomRandomFeatureConfig) {
            multiChance((RandomRandomFeatureConfig) feature.config, errors);
            return;
        }

        checkFeature(feature.feature, errors);
        checkConfig(feature.config, errors);
    }

    private static void decorated(DecoratedFeatureConfig config, List<String> errors) {
        checkConfiguredFeature(config.feature, errors);
        checkDecorator(config.decorator, errors);
    }

    private static void single(SimpleRandomFeatureConfig config, List<String> errors) {
        for (ConfiguredFeature<?, ?> feature : config.features) {
            checkConfiguredFeature(feature, errors);
        }
    }

    private static void twoChoice(RandomBooleanFeatureConfig config, List<String> errors) {
        checkConfiguredFeature(config.featureTrue, errors);
        checkConfiguredFeature(config.featureFalse, errors);
    }

    private static void multi(RandomFeatureConfig config, List<String> errors) {
        for (RandomFeatureEntry<?> feature : config.features) {
            checkConfiguredFeature(feature.feature, errors);
        }
    }

    private static void multiChance(RandomRandomFeatureConfig config, List<String> errors) {
        for (ConfiguredFeature<?, ?> feature : config.features) {
            checkConfiguredFeature(feature, errors);
        }
    }

    private static void checkFeature(Feature<?> feature, List<String> list) {
        if (feature == null) {
            list.add("null feature");
        } else if (Registry.FEATURE.getId(feature) == null) {
            list.add("unregistered feature: " + feature.getClass().getName());
        }
    }

    private static void checkConfig(FeatureConfig config, List<String> list) {
        if (config == null) {
            list.add("null config");
            return;
        }

        try {
            config.serialize(JsonOps.INSTANCE);
        } catch (Throwable t) {
            list.add("config: " + config.getClass().getName() + ", error: " + t.getMessage());
        }
    }

    private static void checkDecorator(ConfiguredDecorator<?> decorator, List<String> list) {
        if (decorator == null) {
            list.add("null configured placement");
            return;
        }

        if (decorator.decorator == null) {
            list.add("null placement");
        } else if (Registry.DECORATOR.getId(decorator.decorator) == null) {
            list.add("unregistered placement: " + decorator.decorator.getClass().getName());
        }

        if (decorator.config == null) {
            list.add("null decorator config");
        } else {
            try {
                decorator.config.serialize(JsonOps.INSTANCE);
            } catch (Throwable t) {
                list.add("placement config: " + decorator.config.getClass().getName() + ", error: " + t.getMessage());
            }
        }
    }
}
