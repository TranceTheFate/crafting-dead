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

package com.craftingdead.immerse.game.module.shop.message;

import net.minecraft.network.PacketBuffer;

public class SyncUserMessage {

  private final int buyTimeSeconds;
  private final int money;

  public SyncUserMessage(int buyTimeSeconds, int money) {
    this.buyTimeSeconds = buyTimeSeconds;
    this.money = money;
  }

  public int getBuyTimeSeconds() {
    return this.buyTimeSeconds;
  }

  public int getMoney() {
    return this.money;
  }

  public void encode(PacketBuffer out) {
    out.writeVarInt(this.buyTimeSeconds);
    out.writeVarInt(this.money);
  }

  public static SyncUserMessage decode(PacketBuffer in) {
    return new SyncUserMessage(in.readVarInt(), in.readVarInt());
  }
}
