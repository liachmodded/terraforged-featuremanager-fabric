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

package com.terraforged.feature;

import com.terraforged.feature.biome.BiomeFeature;
import com.terraforged.feature.biome.BiomeFeatures;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.ChunkRegion;

public interface FeatureDecorator {

    FeatureManager getFeatureManager();

    default void decorate(ChunkGenerator<?> generator, ChunkRegion region) {
        int chunkX = region.getCenterChunkX();
        int chunkZ = region.getCenterChunkZ();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        Chunk chunk = region.getChunk(chunkX, chunkZ);

        BlockPos pos = new BlockPos(blockX, 0, blockZ);
        Biome biome = region.getBiomeAccess().getBiome(pos.add(8, 8, 8));

        decorate(generator, region, chunk, biome, pos);
    }

    default void decorate(ChunkGenerator<?> generator, IWorld region, Chunk chunk, Biome biome, BlockPos pos) {
        ChunkRandom random = new ChunkRandom();
        long populationSeed = random.setPopulationSeed(region.getSeed(), pos.getX(), pos.getZ());

        BiomeFeatures features = getFeatureManager().getFeatures(biome);
        for (GenerationStep.Feature stage : GenerationStep.Feature.values()) {
            int featureOrdinal = 0;
            for (BiomeFeature feature : features.getStage(stage)) {
                random.setDecoratorSeed(populationSeed, featureOrdinal++, stage.ordinal());

                if (feature.getPredicate().test(chunk, biome)) {
                    feature.getFeature().generate(region, generator, random, pos);
                }
            }
        }
    }
}
