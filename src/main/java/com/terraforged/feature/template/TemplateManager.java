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

package com.terraforged.feature.template;

import com.terraforged.feature.FeatureManager;
import com.terraforged.feature.data.DataHelper;
import com.terraforged.feature.data.DataPack;
import com.terraforged.feature.template.decorator.DecoratedFeature;
import com.terraforged.feature.template.decorator.DecoratorFactory;
import com.terraforged.feature.template.feature.MultiTemplateFeature;
import com.terraforged.feature.template.feature.TemplateFeature;
import com.terraforged.feature.template.type.FeatureTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemplateManager {

    private static final Marker marker = MarkerManager.getMarker("TEMPLATES");

    public static void register() {
        ResourceManager manager = DataHelper.getResourceManager();
        List<TemplateConfig> configs = loadConfigs(manager);
        for (TemplateConfig config : configs) {
            FeatureManager.LOG.debug(marker, "Registering feature: {}", config.getRegistryName());
            List<TemplateFeature> templates = loadTemplates(manager, config);
            MultiTemplateFeature feature = new MultiTemplateFeature(config, templates);
            DecoratorFactory factory = feature.getType().getFactory();
            Optional<DecoratedFeature<?, ?>> decorated = factory.apply(feature, config.getDecorators());
            if (decorated.isPresent()) {
                Registry.register(Registry.FEATURE, feature.getName(), decorated.get());
                FeatureTypes.register(feature.getType(), decorated.get());
            } else {
                Registry.register(Registry.FEATURE, feature.getName(), feature);
                FeatureTypes.register(feature.getType(), feature);
            }
        }
    }

    private static List<TemplateConfig> loadConfigs(ResourceManager manager) {
        List<TemplateConfig> list = new ArrayList<>();
        DataPack.iterateJson(manager, "templates", (location, element) -> {
            Optional<TemplateConfig> config = TemplateConfig.parse(location, element);
            if (config.isPresent()) {
                list.add(config.get());
                FeatureManager.LOG.debug(marker, " Loaded template config: {}", location);
            } else {
                FeatureManager.LOG.error(marker, " Failed to load template config: {}", location);
            }
        });
        return list;
    }

    private static List<TemplateFeature> loadTemplates(ResourceManager manager, TemplateConfig config) {
        List<TemplateFeature> list = new ArrayList<>();
        for (Identifier path : config.getPaths()) {
            FeatureManager.LOG.debug(marker, " Loading templates for: {}", config.getRegistryName());
            DataPack.iterateData(manager, path.getPath(), DataHelper.NBT, (location, data) -> {
                Optional<TemplateFeature> template = TemplateFeature.load(data);
                if (template.isPresent()) {
                    list.add(template.get());
                    FeatureManager.LOG.debug(marker, "  Loaded template: {}", location);
                } else {
                    FeatureManager.LOG.error(marker, "  Failed to load template: {}", location);
                }
            });
        }
        return list;
    }
}
