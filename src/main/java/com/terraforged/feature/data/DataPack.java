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
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public final class DataPack {

    private DataPack() {
    }

    public static void iterateData(ResourceManager manager, String path, Predicate<String> matcher, ResourceVisitor<InputStream> consumer) {
        try {
            for (Identifier location : manager.findResources(path, matcher)) {
                try (Resource data = manager.getResource(location)) {
                    consumer.accept(location, data.getInputStream());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void iterateJson(ResourceManager manager, String path, ResourceVisitor<JsonElement> consumer) {
        JsonParser parser = new JsonParser();
        iterateData(manager, path, DataHelper.JSON, (location, data) -> {
            try (Reader reader = new BufferedReader(new InputStreamReader(data))) {
                JsonElement element = parser.parse(reader);
                consumer.accept(location, element);
            }
        });
    }
}
