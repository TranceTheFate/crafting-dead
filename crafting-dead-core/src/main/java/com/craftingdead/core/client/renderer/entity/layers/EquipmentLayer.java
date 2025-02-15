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

package com.craftingdead.core.client.renderer.entity.layers;

import org.apache.commons.lang3.Validate;
import com.craftingdead.core.capability.Capabilities;
import com.craftingdead.core.client.util.RenderUtil;
import com.craftingdead.core.world.inventory.ModEquipmentSlotType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.TransformationMatrix;

/**
 * Layer that renders {@link IEquipableModel}s attached to a player's body.
 */
public class EquipmentLayer<T extends LivingEntity, M extends BipedModel<T>>
    extends LayerRenderer<T, M> {

  private final ModEquipmentSlotType slot;

  /**
   * Whether this model should be rotated when the player is crouching.
   */
  private final boolean useCrouchingOrientation;

  /**
   * Whether this model should be rotated accordingly to the player's head.
   */
  private final boolean useHeadOrientation;

  /**
   * Optional arbitrary transformation right before rendering the {@link ItemStack}.
   */
  private final TransformationMatrix transformation;

  private EquipmentLayer(Builder<T, M> builder) {
    super(builder.entityRenderer);
    this.slot = builder.slot;
    this.useCrouchingOrientation = builder.useCrouchingOrientation;
    this.transformation = builder.tranformation;
    this.useHeadOrientation = builder.useHeadOrientation;
  }

  @Override
  public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer,
      int packedLight, T livingEntity, float limbSwing, float limbSwingAmount,
      float partialTicks, float ageTicks, float headYaw, float headPitch) {

    Minecraft minecraft = Minecraft.getInstance();
    boolean invisible = livingEntity.isInvisible();
    boolean partiallyVisible =
        livingEntity.isInvisible() && !livingEntity.isInvisibleTo(minecraft.player);
    if (partiallyVisible || !invisible) {

      ItemRenderer itemRenderer = minecraft.getItemRenderer();

      livingEntity.getCapability(Capabilities.LIVING_EXTENSION).ifPresent(living -> {

        ItemStack itemStack = living.getItemHandler().getStackInSlot(this.slot.getIndex());

        if (!itemStack.isEmpty()) {
          IBakedModel bakedModel =
              itemRenderer.getModel(itemStack, livingEntity.level, livingEntity);

          matrixStack.pushPose();

          // Applies crouching rotation is needed
          if (this.useCrouchingOrientation && livingEntity.isCrouching()) {
            RenderUtil.applyPlayerCrouchRotation(matrixStack);
          }

          // Applies the head orientation if needed
          if (this.useHeadOrientation) {
            // Vanilla's transformation for child entities, like baby zombies
            if (livingEntity.isBaby()) {
              matrixStack.translate(0.0D, 0.03125D, 0.0D);
              matrixStack.scale(0.7F, 0.7F, 0.7F);
              matrixStack.translate(0.0D, 1.0D, 0.0D);
            }

            this.getParentModel().getHead().translateAndRotate(matrixStack);
          }

          // Applies the arbitrary transformation if needed
          if (this.transformation != null) {
            this.transformation.push(matrixStack);
          }

          // Renders the item. Also note the TransformType.
          itemRenderer.render(itemStack, ItemCameraTransforms.TransformType.HEAD, false,
              matrixStack, renderTypeBuffer, packedLight, OverlayTexture.NO_OVERLAY, bakedModel);

          if (this.transformation != null) {
            matrixStack.popPose();
          }

          matrixStack.popPose();
        }
      });
    }
  }

  public static class Builder<T extends LivingEntity, M extends BipedModel<T>> {

    private LivingRenderer<T, M> entityRenderer;
    private ModEquipmentSlotType slot;
    private TransformationMatrix tranformation;
    private boolean useCrouchingOrientation;
    private boolean useHeadOrientation;

    public Builder<T, M> withRenderer(LivingRenderer<T, M> entityRenderer) {
      this.entityRenderer = entityRenderer;
      return this;
    }

    public Builder<T, M> withSlot(ModEquipmentSlotType slot) {
      this.slot = slot;
      return this;
    }

    public Builder<T, M> withArbitraryTransformation(TransformationMatrix transformation) {
      this.tranformation = transformation;
      return this;
    }

    public Builder<T, M> withCrouchingOrientation(boolean useCrouchingOrientation) {
      this.useCrouchingOrientation = useCrouchingOrientation;
      return this;
    }

    public Builder<T, M> withHeadOrientation(boolean useHeadOrientation) {
      this.useHeadOrientation = useHeadOrientation;
      return this;
    }

    public EquipmentLayer<T, M> build() {
      Validate.notNull(this.entityRenderer, "The renderer must not be null");
      Validate.notNull(this.slot, "The slot must not be null");
      return new EquipmentLayer<>(this);
    }
  }
}
