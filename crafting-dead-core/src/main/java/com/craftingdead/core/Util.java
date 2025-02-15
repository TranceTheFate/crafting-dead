package com.craftingdead.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.mojang.serialization.Codec;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class Util {

  public static <T> Optional<T> optional(@Nullable Supplier<T> supplier) {
    return Optional.ofNullable(supplier).map(Supplier::get);
  }

  public static <T> Supplier<T> supply(T value) {
    return () -> value;
  }

  public static <T> Codec<Set<T>> setOf(Codec<T> codec) {
    return codec.listOf().xmap(HashSet::new, ArrayList::new);
  }

  public static <T> RegistryKey<T> createKey(
      RegistryKey<? extends Registry<T>> registryKey, String name) {
    return RegistryKey.create(registryKey, new ResourceLocation(CraftingDead.ID, name));
  }

  public static <T> RegistryKey<Registry<T>> createRegistryKey(String name) {
    return RegistryKey.createRegistryKey(new ResourceLocation(CraftingDead.ID, name));
  }
}
