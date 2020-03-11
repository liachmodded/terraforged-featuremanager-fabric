/*
 *
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.terraforged.feature.matcher.dynamic;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;

import java.util.function.Predicate;

public class DynamicMatcher implements Predicate<ConfiguredFeature<?, ?>> {

    public static final DynamicMatcher NONE = DynamicMatcher.of(f -> false);

    private final Predicate<ConfiguredFeature<?, ?>> predicate;

    private DynamicMatcher(Predicate<ConfiguredFeature<?, ?>> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(ConfiguredFeature<?, ?> feature) {
        if (feature.config instanceof DecoratedFeatureConfig) {
            return decorated((DecoratedFeatureConfig) feature.config);
        }

        // note SimpleRandomFeatureConfig & SimpleRandomFeatureConfigConfig names a mixed up
        if (feature.config instanceof SimpleRandomFeatureConfig) {
            return single((SimpleRandomFeatureConfig) feature.config);
        }

        if (feature.config instanceof RandomBooleanFeatureConfig) {
            return twoChoice((RandomBooleanFeatureConfig) feature.config);
        }

        if (feature.config instanceof RandomFeatureConfig) {
            return multi((RandomFeatureConfig) feature.config);
        }

        if (feature.config instanceof RandomRandomFeatureConfig) {
            return multiChance((RandomRandomFeatureConfig) feature.config);
        }

        return predicate.test(feature);
    }

    private boolean decorated(DecoratedFeatureConfig config) {
        return test(config.feature);
    }

    private boolean single(SimpleRandomFeatureConfig config) {
        for (ConfiguredFeature<?, ?> feature : config.features) {
            if (test(feature)) {
                return true;
            }
        }
        return false;
    }

    private boolean twoChoice(RandomBooleanFeatureConfig config) {
        if (test(config.featureTrue)) {
            return true;
        }
        return test(config.featureFalse);
    }

    private boolean multi(RandomFeatureConfig config) {
        for (RandomFeatureEntry<?> feature : config.features) {
            if (test(feature.feature)) {
                return true;
            }
        }
        return false;
    }

    private boolean multiChance(RandomRandomFeatureConfig config) {
        for (ConfiguredFeature<?, ?> feature : config.features) {
            if (test(feature)) {
                return true;
            }
        }
        return false;
    }

    public static DynamicMatcher of(Predicate<ConfiguredFeature<?, ?>> predicate) {
        return new DynamicMatcher(predicate);
    }

    public static DynamicMatcher feature(Predicate<Feature<?>> predicate) {
        return DynamicMatcher.of(f -> predicate.test(f.feature));
    }

    public static DynamicMatcher feature(Feature<?> feature) {
        return DynamicMatcher.feature(f -> f == feature);
    }

    public static DynamicMatcher feature(Class<? extends Feature> type) {
        return DynamicMatcher.feature(type::isInstance);
    }

    public static DynamicMatcher config(Predicate<FeatureConfig> predicate) {
        return DynamicMatcher.of(f -> predicate.test(f.config));
    }

    public static DynamicMatcher config(FeatureConfig config) {
        return DynamicMatcher.of(f -> f.config == config);
    }

    public static DynamicMatcher config(Class<? extends FeatureConfig> type) {
        return DynamicMatcher.config(type::isInstance);
    }
}
