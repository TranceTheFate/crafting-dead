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

package com.craftingdead.core.network.message.play;

import java.util.function.Supplier;
import com.craftingdead.core.capability.Capabilities;
import com.craftingdead.core.network.NetworkUtil;
import com.craftingdead.core.world.gun.Gun;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncGunContainerSlotMessage {

  private final int entityId;
  private final int slot;
  private final PacketBuffer data;

  public SyncGunContainerSlotMessage(int entityId, int slot, Gun gun, boolean writeAll) {
    this(entityId, slot, new PacketBuffer(Unpooled.buffer()));
    gun.encode(this.data, writeAll);
  }

  public SyncGunContainerSlotMessage(int entityId, int slot, PacketBuffer data) {
    this.entityId = entityId;
    this.slot = slot;
    this.data = data;
  }

  public void encode(PacketBuffer out) {
    out.writeVarInt(this.entityId);
    out.writeShort(this.slot);
    out.writeVarInt(this.data.readableBytes());
    out.writeBytes(this.data);
  }

  public static SyncGunContainerSlotMessage decode(PacketBuffer in) {
    int entityId = in.readVarInt();
    int slot = in.readShort();
    byte[] data = new byte[in.readVarInt()];
    in.readBytes(data);
    return new SyncGunContainerSlotMessage(entityId, slot,
        new PacketBuffer(Unpooled.wrappedBuffer(data)));
  }

  public boolean handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> NetworkUtil.getEntity(
        ctx.get(), this.entityId, PlayerEntity.class).inventoryMenu.getSlot(this.slot).getItem()
            .getCapability(Capabilities.GUN)
            .ifPresent(gun -> gun.decode(this.data)));
    return true;
  }
}
