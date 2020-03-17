package com.terraforged.feature.predicate;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;

import java.util.HashSet;
import java.util.Set;

public class BiomePredicate implements FeaturePredicate {

    private final Set<Biome> biomes;

    private BiomePredicate(Set<Biome> biomes) {
        this.biomes = biomes;
    }

    @Override
    public boolean test(Chunk chunk, Biome biome) {
        BiomeArray biomes = chunk.getBiomeArray();
        if (biomes == null) {
            return false;
        }
        for (int dz = 4; dz < 16; dz += 8) {
            for (int dx = 4; dx < 16; dx += 8) {
                Biome b = biomes.getBiomeForNoiseGen(dx, 0, dz);
                if (!this.biomes.contains(b)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static BiomePredicate oceans() {
        return BiomePredicate.of(Biome.Category.OCEAN);
    }

    public static BiomePredicate of(Biome.Category... categories) {
        Set<Biome> set = new HashSet<>();
        for (Biome biome : Registry.BIOME) {
            for (Biome.Category category : categories) {
                if (biome.getCategory() == category) {
                    set.add(biome);
                    break;
                }
            }
        }
        return new BiomePredicate(set);
    }
}
