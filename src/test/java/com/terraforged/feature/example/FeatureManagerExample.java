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

package com.terraforged.feature.example;

import com.terraforged.feature.event.FeatureModifierEvent;
import com.terraforged.feature.matcher.feature.FeatureMatcher;
import com.terraforged.feature.modifier.FeatureModifiers;
import com.terraforged.feature.transformer.FeatureTransformer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

public class FeatureManagerExample implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("REGISTERING WORLD TYPE");
        FeatureModifierEvent.EVENT.register(FeatureManagerExample::registerFeatureModifier);
    }

    private static void registerFeatureModifier(IWorld world, FeatureModifiers modifiers) {
        if (world.getDimension().getType() == DimensionType.OVERWORLD) {
            System.out.println("REGISTERING TRANSFORMER");
            modifiers.getTransformers().add(
                    // match any feature that uses oak_leaves & oak_log blocks (ie oak trees)
                    FeatureMatcher.and("minecraft:oak_leaves", "minecraft:oak_log"),
                    // replace any occurrence of "minecraft:oak_leaves" with "minecraft:gold_block"
                    FeatureTransformer.replace("minecraft:oak_leaves", "minecraft:gold_block")
            );
        }
    }
}
