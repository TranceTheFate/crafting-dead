/*
 * Crafting Dead
 * Copyright (C) 2022  NexusNode LTD
 *
 * This Non-Commercial Software License Agreement (the "Agreement") is made between
 * you (the "Licensee") and NEXUSNODE (BRAD HUNTER). (the "Licensor").
 * By installing or otherwise using Crafting Dead (the "Software"), you agree to be
 * bound by the terms and conditions of this Agreement as may be revised from time
 * to time at Licensor's sole discretion.
 *
 * If you do not agree to the terms and conditions of this Agreement do not download,
 * copy, reproduce or otherwise use any of the source code available online at any time.
 *
 * https://github.com/nexusnode/crafting-dead/blob/1.18.x/LICENSE.txt
 *
 * https://craftingdead.net/terms.php
 */

package com.craftingdead.immerse.client.gui.view.style;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionException;
import com.craftingdead.immerse.CraftingDeadImmerse;
import com.craftingdead.immerse.client.gui.view.style.parser.StyleSheetParser;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.Typeface;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class StyleSheetManager
    extends SimplePreparableReloadListener<Map<ResourceLocation, StyleList>> {

  private static final ResourceLocation USER_AGENT =
      new ResourceLocation(CraftingDeadImmerse.ID, "user-agent");

  private static StyleSheetManager instance;

  private Map<ResourceLocation, StyleList> styleLists = Collections.emptyMap();

  public StyleList createStyleList(Iterable<ResourceLocation> styleSheets) {
    var userAgentList = this.styleLists.get(USER_AGENT);
    var list = userAgentList == null ? new StyleList() : new StyleList(userAgentList);
    for (var name : styleSheets) {
      var styleSheet = this.styleLists.get(name);
      if (styleSheet == null) {
        throw new IllegalArgumentException("Unknown style sheet: " + name);
      }
      list.merge(styleSheet);
    }
    return list;
  }

  @Override
  protected Map<ResourceLocation, StyleList> prepare(ResourceManager resourceManager,
      ProfilerFiller profilerFiller) {
    var resources = resourceManager.listResources("css", resource -> resource.endsWith(".css"));
    Map<ResourceLocation, StyleList> styleLists = new HashMap<>();
    Multimap<StyleList, ResourceLocation> pendingMerge = HashMultimap.create();

    for (var resource : resources) {
      var path = resource.getPath();
      var name = new ResourceLocation(resource.getNamespace(),
          path.substring("css/".length(), path.length() - ".css".length()));
      try (var inputStream = resourceManager.getResource(resource).getInputStream()) {
        var result = StyleSheetParser.loadStyleSheet(inputStream,
            fontLocation -> loadFont(resourceManager, fontLocation));
        styleLists.put(name, result.styleList());
        pendingMerge.putAll(result.styleList(), result.dependencies());
      } catch (IOException e) {
        throw new CompletionException(e);
      }
    }

    pendingMerge.forEach((styleList, dependencyLocation) -> {
      var dependency = styleLists.get(dependencyLocation);
      if (dependency == null) {
        throw new IllegalStateException("Style sheet not found: " + dependencyLocation.toString());
      } else {
        styleList.merge(dependency);
      }
    });

    return styleLists;
  }

  @Override
  protected void apply(Map<ResourceLocation, StyleList> styleLists, ResourceManager resourceManager,
      ProfilerFiller profilerFiller) {
    this.styleLists = styleLists;
  }

  private static Typeface loadFont(ResourceManager resourceManager, ResourceLocation location)
      throws IOException {
    try (var inputStream = resourceManager.getResource(location).getInputStream()) {
      return Typeface.makeFromData(Data.makeFromBytes(inputStream.readAllBytes()));
    }
  }

  public static StyleSheetManager getInstance() {
    if (instance == null) {
      instance = new StyleSheetManager();
    }
    return instance;
  }
}
