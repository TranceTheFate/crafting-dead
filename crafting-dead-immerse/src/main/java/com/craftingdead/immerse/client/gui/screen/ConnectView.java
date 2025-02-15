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

package com.craftingdead.immerse.client.gui.screen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.craftingdead.immerse.client.gui.screen.menu.MainMenuView;
import com.craftingdead.immerse.client.gui.view.Color;
import com.craftingdead.immerse.client.gui.view.FogView;
import com.craftingdead.immerse.client.gui.view.ParentView;
import com.craftingdead.immerse.client.gui.view.States;
import com.craftingdead.immerse.client.gui.view.TextView;
import com.craftingdead.immerse.client.gui.view.Transition;
import com.craftingdead.immerse.client.gui.view.ViewScreen;
import com.craftingdead.immerse.client.gui.view.event.ActionEvent;
import com.craftingdead.immerse.client.gui.view.layout.yoga.Align;
import com.craftingdead.immerse.client.gui.view.layout.yoga.Justify;
import com.craftingdead.immerse.client.gui.view.layout.yoga.PositionType;
import com.craftingdead.immerse.client.gui.view.layout.yoga.YogaLayout;
import com.craftingdead.immerse.client.gui.view.layout.yoga.YogaLayoutParent;
import com.craftingdead.immerse.sounds.ImmerseSoundEvents;
import com.google.common.collect.Iterators;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ConnectView extends ParentView<ConnectView, ViewScreen, YogaLayout> {

  private static final Logger logger = LogManager.getLogger();
  private static final ExecutorService executorService = Executors.newSingleThreadExecutor(
      new ThreadFactoryBuilder()
          .setNameFormat("server-connector-%s")
          .setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(logger))
          .build());

  private final Iterator<String> animation = Iterators.cycle("O o o", "o O o", "o o O");
  private long lastAnimationUpdateMs;

  private NetworkManager connection;
  private boolean aborted;
  private final Screen lastScreen;
  private final TextView<YogaLayout> statusView;
  private final TextView<YogaLayout> animationView;

  public ConnectView(ViewScreen layout, Screen lastScreen, String host, int port) {
    super(layout,
        new YogaLayoutParent().setAlignItems(Align.CENTER).setJustifyContent(Justify.CENTER));
    this.lastScreen = lastScreen;

    this.addChild(MainMenuView.createBackgroundView());
    this.addChild(new FogView<>(new YogaLayout()
        .setPositionType(PositionType.ABSOLUTE)
        .setHeightPercent(100)
        .setWidthPercent(100)));

    this.addChild(
        new ParentView<>(new YogaLayout().setWidthPercent(60).setHeightPercent(60),
            new YogaLayoutParent()
                .setAlignItems(Align.CENTER)
                .setJustifyContent(Justify.CENTER))
                    .setBackgroundBlur(50.0F)
                    .configure(
                        view -> view.getBackgroundColorProperty()
                            .setBaseValue(new Color(0x40111111)))
                    .addChild(
                        this.statusView =
                            new TextView<>(new YogaLayout().setWidthPercent(100).setHeight(15),
                                new TranslationTextComponent("connect.connecting"))
                                    .setCentered(true))
                    .addChild(
                        this.animationView =
                            new TextView<>(new YogaLayout().setWidthPercent(100).setHeight(15),
                                this.nextAnimation()).setCentered(true))
                    .addChild(
                        new TextView<>(
                            new YogaLayout().setWidth(150).setHeight(20).setTopMargin(50),
                            DialogTexts.GUI_CANCEL)
                                .setCentered(true)
                                .configure(view -> view.getBackgroundColorProperty()
                                    .setBaseValue(
                                        new Color(TextFormatting.RED.getColor() + (100 << 24)))
                                    .registerState(
                                        new Color(TextFormatting.DARK_RED.getColor() + (100 << 24)),
                                        States.HOVERED, States.ENABLED)
                                    .setTransition(Transition.linear(150L)))
                                .addActionSound(ImmerseSoundEvents.BUTTON_CLICK.get())
                                .addListener(ActionEvent.class, (c, e) -> {
                                  this.aborted = true;
                                  if (this.connection != null) {
                                    this.connection.disconnect(
                                        new TranslationTextComponent("connect.aborted"));
                                  }
                                  this.minecraft.setScreen(this.lastScreen);
                                })));

    this.minecraft.clearLevel();
    this.connect(host, port);
  }

  private void connect(final String host, final int port) {
    logger.info("Connecting to {}, {}", host, port);
    executorService.submit(() -> {
      InetAddress address = null;

      try {
        if (ConnectView.this.aborted) {
          return;
        }

        address = InetAddress.getByName(host);
        ConnectView.this.connection = NetworkManager.connectToServer(address,
            port, ConnectView.this.minecraft.options.useNativeTransport());
        ConnectView.this.connection.setListener(
            new ClientLoginNetHandler(ConnectView.this.connection,
                ConnectView.this.minecraft, ConnectView.this.lastScreen,
                ConnectView.this.statusView::setText));
        ConnectView.this.connection
            .send(new CHandshakePacket(host, port, ProtocolType.LOGIN));
        ConnectView.this.connection.send(
            new CLoginStartPacket(ConnectView.this.minecraft.getUser().getGameProfile()));
      } catch (UnknownHostException e) {
        if (ConnectView.this.aborted) {
          return;
        }

        ConnectView.logger.error("Couldn't connect to server", e);
        ConnectView.this.minecraft.execute(() -> ConnectView.this.minecraft.setScreen(
            new DisconnectedScreen(ConnectView.this.lastScreen, DialogTexts.CONNECT_FAILED,
                new TranslationTextComponent("disconnect.genericReason", "Unknown host"))));
      } catch (Exception e) {
        if (ConnectView.this.aborted) {
          return;
        }

        ConnectView.logger.error("Couldn't connect to server", e);
        String censoredReason = address == null
            ? e.toString()
            : e.toString().replaceAll(address + ":" + port, "");
        ConnectView.this.minecraft.execute(() -> ConnectView.this.minecraft.setScreen(
            new DisconnectedScreen(ConnectView.this.lastScreen, DialogTexts.CONNECT_FAILED,
                new TranslationTextComponent("disconnect.genericReason", censoredReason))));
      }
    });
  }

  @Override
  public void tick() {
    super.tick();
    if (this.connection != null) {
      if (this.connection.isConnected()) {
        this.connection.tick();
      } else {
        this.connection.handleDisconnection();
      }
    }
    long currentTime = Util.getMillis();
    if (this.lastAnimationUpdateMs != -1L && currentTime - this.lastAnimationUpdateMs >= 100L) {
      this.lastAnimationUpdateMs = currentTime;
      this.animationView.setText(this.nextAnimation());
    }
  }

  private ITextComponent nextAnimation() {
    return new StringTextComponent(this.animation.next()).withStyle(TextFormatting.GRAY);
  }

  public static Screen createScreen(Screen lastScreen, String host, int port) {
    return new ViewScreen(NarratorChatListener.NO_TITLE,
        layout -> new ConnectView(layout, lastScreen, host, port));
  }
}
