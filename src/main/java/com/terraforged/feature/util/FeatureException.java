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

package com.terraforged.feature.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;

public class FeatureException extends RuntimeException {

    public FeatureException(ConfiguredFeature<?, ?> feature, Throwable t) {
        super(feature(feature), t);
    }

    public FeatureException(JsonElement element, Throwable t) {
        super(element(element), t);
    }

    public static String getName(ConfiguredFeature<?, ?> feature) {
        StringBuilder sb = new StringBuilder(256);
        toString(feature, sb);
        return sb.toString();
    }

    private static String feature(ConfiguredFeature<?, ?> feature) {
        StringBuilder sb = new StringBuilder(256);
        toString(feature, sb);
        return String.format("Failed to serialize Feature: %s", sb.toString());
    }

    private static String element(JsonElement element) {
        return String.format("Failed to deserialize Feature data: %s", element);
    }

    private static void toString(ConfiguredFeature<?, ?> feature, StringBuilder sb) {
        if (feature.config instanceof DecoratedFeatureConfig) {
            decorated((DecoratedFeatureConfig) feature.config, sb);
            return;
        }

        // note SimpleRandomFeatureConfig & SimpleRandomFeatureConfigConfig names a mixed up
        if (feature.config instanceof SimpleRandomFeatureConfig) {
            single((SimpleRandomFeatureConfig) feature.config, sb);
            return;
        }

        if (feature.config instanceof RandomBooleanFeatureConfig) {
            twoChoice((RandomBooleanFeatureConfig) feature.config, sb);
            return;
        }

        if (feature.config instanceof RandomFeatureConfig) {
            multi((RandomFeatureConfig) feature.config, sb);
            return;
        }

        if (feature.config instanceof RandomRandomFeatureConfig) {
            multiChance((RandomRandomFeatureConfig) feature.config, sb);
            return;
        }

        sb.append(Registry.FEATURE.getId(feature.feature));
        sb.append('(').append(config(feature.config)).append(')');
    }

    private static void decorated(DecoratedFeatureConfig config, StringBuilder sb) {
        sb.append("Decorated{");
        toString(config.feature, sb);
        sb.append("}");
    }

    private static void single(SimpleRandomFeatureConfig config, StringBuilder sb) {
        sb.append("Single[");
        for (ConfiguredFeature<?, ?> feature : config.features) {
            toString(feature, sb);
        }
        sb.append("]");
    }

    private static void twoChoice(RandomBooleanFeatureConfig config, StringBuilder sb) {
        sb.append("Choice{");
        {
            sb.append("a={");
            toString(config.featureTrue, sb);
            sb.append("},b={");
            toString(config.featureFalse, sb);
            sb.append("}");
        }
        sb.append("}");
    }

    private static void multi(RandomFeatureConfig config, StringBuilder sb) {
        sb.append("Multi[");
        int start = sb.length();
        for (RandomFeatureEntry<?> feature : config.features) {
            comma(sb, start);
            toString(feature.feature, sb);
        }
        sb.append("]");
    }

    private static void multiChance(RandomRandomFeatureConfig config, StringBuilder sb) {
        sb.append("Chance[");
        int start = sb.length();
        for (ConfiguredFeature<?, ?> feature : config.features) {
            comma(sb, start);
            toString(feature, sb);
        }
        sb.append("]");
    }

    private static void comma(StringBuilder sb, int len) {
        if (sb.length() > len) {
            sb.append(',');
        }
    }

    private static String config(FeatureConfig config) {
        try {
            return config.serialize(JsonOps.INSTANCE).getValue().toString();
        } catch (Throwable t) {
            return config + "";
        }
    }
}
