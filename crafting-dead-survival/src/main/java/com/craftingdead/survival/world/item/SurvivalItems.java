/*
 * Crafting Dead
 * Copyright (C) 2021  NexusNode LTD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.craftingdead.survival.world.item;

import com.craftingdead.core.world.item.ActionItem;
import com.craftingdead.core.world.item.GrenadeItem;
import com.craftingdead.survival.CraftingDeadSurvival;
import com.craftingdead.survival.world.action.SurvivalActionTypes;
import com.craftingdead.survival.world.entity.SurvivalEntityTypes;
import com.craftingdead.survival.world.entity.grenade.PipeGrenadeEntity;
import com.craftingdead.survival.world.level.block.SurvivalBlocks;
import com.craftingdead.survival.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SurvivalItems {

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, CraftingDeadSurvival.ID);

  public static final ItemGroup TAB = new ItemGroup(CraftingDeadSurvival.ID) {

    @Override
    public ItemStack makeIcon() {
      return new ItemStack(RBI_SYRINGE::get);
    }
  };

  // ================================================================================
  // Loot
  // ================================================================================

  public static final RegistryObject<Item> MILITARY_LOOT_ITEM =
      ITEMS.register("military_loot",
          () -> new BlockItem(SurvivalBlocks.MILITARY_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> MEDIC_LOOT_ITEM =
      ITEMS.register("medic_loot",
          () -> new BlockItem(SurvivalBlocks.MEDICAL_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> CIVILIAN_LOOT_ITEM =
      ITEMS.register("civilian_loot",
          () -> new BlockItem(SurvivalBlocks.CIVILIAN_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> CIVILIAN_RARE_LOOT_ITEM =
      ITEMS.register("civilian_rare_loot",
          () -> new BlockItem(SurvivalBlocks.RARE_CIVILIAN_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> POLICE_LOOT_ITEM =
      ITEMS.register("police_loot",
          () -> new BlockItem(SurvivalBlocks.POLICE_LOOT.get(), new Item.Properties()
              .rarity(Rarity.EPIC)
              .tab(TAB)));

  public static final RegistryObject<Item> MILITARY_LOOT_GEN_ITEM =
      ITEMS.register("military_loot_gen",
          () -> new BlockItem(SurvivalBlocks.MILITARY_LOOT_GENERATOR.get(), new Item.Properties()
              .tab(TAB)));

  public static final RegistryObject<Item> MEDIC_LOOT_GEN_ITEM =
      ITEMS.register("medic_loot_gen",
          () -> new BlockItem(SurvivalBlocks.MEDICAL_LOOT_GENERATOR.get(), new Item.Properties()
              .tab(TAB)));

  public static final RegistryObject<Item> CIVILIAN_LOOT_GEN_ITEM =
      ITEMS.register("civilian_loot_gen",
          () -> new BlockItem(SurvivalBlocks.CIVILIAN_LOOT_GENERATOR.get(), new Item.Properties()
              .tab(TAB)));

  public static final RegistryObject<Item> CIVILIAN_RARE_LOOT_GEN_ITEM =
      ITEMS.register("civilian_rare_loot_gen",
          () -> new BlockItem(SurvivalBlocks.RARE_CIVILIAN_LOOT_GENERATOR.get(),
              new Item.Properties()
                  .tab(TAB)));

  public static final RegistryObject<Item> POLICE_LOOT_GEN_ITEM =
      ITEMS.register("police_loot_gen",
          () -> new BlockItem(SurvivalBlocks.POLICE_LOOT_GENERATOR.get(), new Item.Properties()
              .tab(TAB)));

  // ================================================================================
  // Supply Drop Radio
  // ================================================================================

  public static final RegistryObject<Item> MEDICAL_DROP_RADIO =
      ITEMS.register("medical_drop_radio",
          () -> new SupplyDropRadioItem(
              (SupplyDropRadioItem.Properties) new SupplyDropRadioItem.Properties()
                  .setLootTable(BuiltInLootTables.MEDICAL_SUPPLY_DROP)
                  .stacksTo(1)
                  .tab(TAB)));

  public static final RegistryObject<Item> MILITARY_DROP_RADIO =
      ITEMS.register("military_drop_radio",
          () -> new SupplyDropRadioItem(
              (SupplyDropRadioItem.Properties) new SupplyDropRadioItem.Properties()
                  .setLootTable(BuiltInLootTables.MILITARY_SUPPLY_DROP)
                  .stacksTo(1)
                  .tab(TAB)));

  // ================================================================================
  // Virus
  // ================================================================================

  public static final RegistryObject<GrenadeItem> PIPE_GRENADE = ITEMS.register("pipe_grenade",
      () -> new GrenadeItem((GrenadeItem.Properties) new GrenadeItem.Properties()
          .setGrenadeEntitySupplier(PipeGrenadeEntity::new)
          .stacksTo(3)
          .tab(TAB)));

  public static final RegistryObject<Item> DIRTY_RAG = ITEMS.register("dirty_rag",
      () -> new ActionItem((ActionItem.Properties) new ActionItem.Properties()
          .setAction(SurvivalActionTypes.WASH_RAG)
          .tab(TAB)));

  public static final RegistryObject<Item> BLOODY_RAG = ITEMS.register("bloody_rag",
      () -> new ActionItem((ActionItem.Properties) new ActionItem.Properties()
          .setAction(SurvivalActionTypes.WASH_RAG)
          .tab(TAB)));

  public static final RegistryObject<Item> CLEAN_RAG = ITEMS.register("clean_rag",
      () -> new ActionItem((ActionItem.Properties) new ActionItem.Properties()
          .setAction(SurvivalActionTypes.USE_CLEAN_RAG)
          .tab(TAB)));

  public static final RegistryObject<Item> SPLINT = ITEMS.register("splint",
      () -> new ActionItem((ActionItem.Properties) new ActionItem.Properties()
          .setAction(SurvivalActionTypes.USE_SPLINT)
          .stacksTo(1)
          .tab(TAB)));

  public static final RegistryObject<Item> RBI_SYRINGE = ITEMS.register("rbi_syringe",
      () -> new ActionItem((ActionItem.Properties) new ActionItem.Properties()
          .setAction(SurvivalActionTypes.USE_RBI_SYRINGE)
          .stacksTo(1)
          .tab(TAB)));

  public static final RegistryObject<Item> CURE_SYRINGE = ITEMS.register("cure_syringe",
      () -> new ActionItem((ActionItem.Properties) new ActionItem.Properties()
          .setAction(SurvivalActionTypes.USE_CURE_SYRINGE)
          .stacksTo(1)
          .tab(TAB)));

  // ================================================================================
  // Spawn Eggs
  // ================================================================================

  public static final RegistryObject<Item> ADVANCED_ZOMBIE_SPAWN_EGG =
      ITEMS.register("advanced_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.ADVANCED_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> FAST_ZOMBIE_SPAWN_EGG =
      ITEMS.register("fast_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.FAST_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> TANK_ZOMBIE_SPAWN_EGG =
      ITEMS.register("tank_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.TANK_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> WEAK_ZOMBIE_SPAWN_EGG =
      ITEMS.register("weak_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.WEAK_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> POLICE_ZOMBIE_SPAWN_EGG =
      ITEMS.register("police_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.POLICE_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));

  public static final RegistryObject<Item> DOCTOR_ZOMBIE_SPAWN_EGG =
      ITEMS.register("doctor_zombie_spawn_egg",
          () -> new ForgeSpawnEggItem(SurvivalEntityTypes.DOCTOR_ZOMBIE, 0x000000, 0xFFFFFF,
              new Item.Properties().tab(TAB)));
}
