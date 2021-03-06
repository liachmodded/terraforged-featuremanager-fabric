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

package com.terraforged.feature.biome;

import net.minecraft.world.gen.GenerationStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BiomeFeatures {

    public static final BiomeFeatures NONE = BiomeFeatures.builder().build();
    private static final List<BiomeFeature> empty = Collections.emptyList();

    private final Map<GenerationStep.Feature, List<BiomeFeature>> features;

    public BiomeFeatures(Builder builder) {
        this.features = builder.features;
        builder.features = Collections.emptyMap();
    }

    public List<BiomeFeature> getStage(GenerationStep.Feature stage) {
        return features.getOrDefault(stage, empty);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Map<GenerationStep.Feature, List<BiomeFeature>> features = Collections.emptyMap();

        public Builder add(GenerationStep.Feature stage, BiomeFeature feature) {
            if (features.isEmpty()) {
                features = new EnumMap<>(GenerationStep.Feature.class);
            }
            features.computeIfAbsent(stage, s -> new ArrayList<>()).add(feature);
            return this;
        }

        public BiomeFeatures build() {
            return new BiomeFeatures(this);
        }
    }
}
