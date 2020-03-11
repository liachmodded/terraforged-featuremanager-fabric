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

package com.terraforged.feature.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DataHelper {

    public static final Predicate<String> ANY = s -> true;
    public static final Predicate<String> NBT = s -> s.endsWith(".nbt");
    public static final Predicate<String> JSON = s -> s.endsWith(".json");

    private static final Supplier<MinecraftServer> serverGetter = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ?
            DataHelper::serverOnClient : DataHelper::serverOnDedicatedServer;

    public static void iterateData(String path, ResourceVisitor<InputStream> consumer) {
        ResourceManager manager = getResourceManager();
        for (Identifier location : manager.findResources(path, JSON)) {
            try (Resource resource = manager.getResource(location)) {
                consumer.accept(location, resource.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void iterateJson(String path, ResourceVisitor<JsonElement> consumer) {
        ResourceManager manager = getResourceManager();
        JsonParser parser = new JsonParser();
        for (Identifier location : manager.findResources(path, JSON)) {
            try (Resource resource = manager.getResource(location)) {
                Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                JsonElement element = parser.parse(reader);
                consumer.accept(location, element);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ResourceManager getResourceManager() {
        return serverGetter.get().getDataManager();
    }

    private static MinecraftServer serverOnClient() {
        return MinecraftClient.getInstance().getServer();
    }

    private static MinecraftServer serverOnDedicatedServer() {
        return (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    }
}
