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

package com.craftingdead.core.world.entity.grenade;

import java.util.Optional;
import org.apache.commons.lang3.tuple.Triple;
import com.craftingdead.core.world.entity.ModEntityTypes;
import com.craftingdead.core.world.item.GrenadeItem;
import com.craftingdead.core.world.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class C4ExplosiveEntity extends GrenadeEntity {

  private static final Triple<SoundEvent, Float, Float> C4_BOUNCE_SOUND =
      Triple.of(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.5F);

  public C4ExplosiveEntity(EntityType<? extends GrenadeEntity> entityIn, World worldIn) {
    super(entityIn, worldIn);
  }

  public C4ExplosiveEntity(LivingEntity thrower, World worldIn) {
    super(ModEntityTypes.C4_EXPLOSIVE.get(), thrower, worldIn);
  }

  @Override
  public boolean hurt(DamageSource source, float amount) {
    if (source.isExplosion() || source.isFire()) {
      // TODO Save who activated the grenade, so the true source
      // of this DamageSource could be used when the grenade explodes.
      this.setActivated(true);
    }
    return super.hurt(source, amount);
  }

  @Override
  public void onGrenadeTick() {}

  @Override
  public void activatedChanged(boolean activated) {
    if (activated) {
      if (!this.level.isClientSide()) {
        this.remove();
        this.level.explode(this,
            this.createDamageSource(),
            null,
            this.getX(), this.getY() + this.getBbHeight(), this.getZ(), 4F, false,
            Explosion.Mode.NONE);
      }
    }
  }

  @Override
  public boolean canBePickedUp(PlayerEntity playerFrom) {
    return this.isStoppedInGround();
  }

  @Override
  public Optional<Float> getBounceFactor(BlockRayTraceResult blockRayTraceResult) {
    return blockRayTraceResult.getDirection() == Direction.UP
        ? Optional.empty()
        : super.getBounceFactor(blockRayTraceResult);
  }

  @Override
  public boolean canBeRemotelyActivated() {
    return true;
  }

  @Override
  public Triple<SoundEvent, Float, Float> getBounceSound(BlockRayTraceResult blockRayTraceResult) {
    return C4_BOUNCE_SOUND;
  }

  @Override
  public GrenadeItem asItem() {
    return ModItems.C4.get();
  }

  @Override
  public void onMotionStop(int stopsCount) {}
}
