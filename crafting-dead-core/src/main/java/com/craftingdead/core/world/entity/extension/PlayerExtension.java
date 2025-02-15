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

package com.craftingdead.core.world.entity.extension;

import javax.annotation.Nonnull;
import com.craftingdead.core.capability.Capabilities;
import com.craftingdead.core.world.inventory.ModEquipmentSlotType;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerExtension<E extends PlayerEntity>
    extends LivingExtension<E, PlayerHandler>, PlayerHandler {

  static <E extends PlayerEntity> PlayerExtension<E> create(E entity) {
    return new PlayerExtensionImpl<>(entity);
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  static <E extends PlayerEntity> PlayerExtension<E> getOrThrow(E player) {
    return Capabilities.getOrThrow(Capabilities.LIVING_EXTENSION, player, PlayerExtension.class);
  }

  boolean isCombatModeEnabled();

  void setCombatModeEnabled(boolean combatModeEnabled);

  void openEquipmentMenu();

  void openStorage(ModEquipmentSlotType slotType);
}
