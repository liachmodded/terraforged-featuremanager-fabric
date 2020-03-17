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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LevelProperties;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class WorldDelegate implements IWorld {

    private IWorld delegate;

    public WorldDelegate(IWorld delegate) {
        this.delegate = delegate;
    }

    public void setDelegate(IWorld delegate) {
        this.delegate = delegate;
    }

    public IWorld getDelegate() {
        return delegate;
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return delegate.isChunkLoaded(chunkX, chunkZ);
    }

    @Override
    public boolean isSkyVisible(BlockPos pos) {
        return delegate.isSkyVisible(pos);
    }

    @Override
    public boolean canPlace(BlockState state, BlockPos pos, EntityContext context) {
        return delegate.canPlace(state, pos, context);
    }

    @Override
    public boolean intersectsEntities(Entity entity) {
        return delegate.intersectsEntities(entity);
    }

    @Override
    public int getLuminance(BlockPos pos) {
        return delegate.getLuminance(pos);
    }

    @Override
    public int getMaxLightLevel() {
        return delegate.getMaxLightLevel();
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }


    @Override
    public long getSeed() {
        return delegate.getSeed();
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return delegate.getBlockTickScheduler();
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return delegate.getFluidTickScheduler();
    }

    @Override
    public World getWorld() {
        return delegate.getWorld();
    }

    @Override
    public LevelProperties getLevelProperties() {
        return delegate.getLevelProperties();
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        return delegate.getLocalDifficulty(pos);
    }

    @Override
    public ChunkManager getChunkManager() {
        return delegate.getChunkManager();
    }

    @Override
    public Random getRandom() {
        return delegate.getRandom();
    }

    @Override
    public void updateNeighbors(BlockPos pos, Block block) {
        delegate.updateNeighbors(pos, block);
    }

    @Override
    public BlockPos getSpawnPos() {
        return delegate.getSpawnPos();
    }

    @Override
    public void playSound(PlayerEntity player, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
        delegate.playSound(player, blockPos, soundEvent, soundCategory, volume, pitch);
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        delegate.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void playLevelEvent(PlayerEntity player, int eventId, BlockPos blockPos, int data) {
        delegate.playLevelEvent(player, eventId, blockPos, data);
    }

    @Override
    public LightingProvider getLightingProvider() {
        return delegate.getLightingProvider();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return delegate.getWorldBorder();
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return delegate.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return delegate.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return delegate.getFluidState(pos);
    }

    @Override
    public List<Entity> getEntities(Entity except, Box box, Predicate<? super Entity> predicate) {
        return delegate.getEntities(except, box, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> entityClass, Box box, Predicate<? super T> predicate) {
        return delegate.getEntities(entityClass, box, predicate);
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return delegate.getPlayers();
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        return delegate.setBlockState(pos, state, flags);
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        return delegate.removeBlock(pos, move);
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, Entity breakingEntity) {
        return delegate.breakBlock(pos, drop, breakingEntity);
    }

    @Override
    public boolean testBlockState(BlockPos blockPos, Predicate<BlockState> state) {
        return delegate.testBlockState(blockPos, state);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        return delegate.getChunk(chunkX, chunkZ, leastStatus, create);
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        return delegate.getTopY(heightmap, x, z);
    }

    @Override
    public int getAmbientDarkness() {
        return delegate.getAmbientDarkness();
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        return delegate.getBiomeAccess();
    }

    @Override
    public Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return delegate.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
    }

    @Override
    public boolean isClient() {
        return delegate.isClient();
    }

    @Override
    public int getSeaLevel() {
        return delegate.getSeaLevel();
    }

    @Override
    public Dimension getDimension() {
        return delegate.getDimension();
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        return delegate.spawnEntity(entity);
    }

    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        return delegate.getColor(pos, colorResolver);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return delegate.getChunk(chunkX, chunkZ);
    }

    @Override
    public BlockView getExistingChunk(int chunkX, int chunkZ) {
        return delegate.getExistingChunk(chunkX, chunkZ);
    }

    @Override
    public float method_24852(Direction direction, boolean bl) {
        return delegate.method_24852(direction, bl);
    }
}
