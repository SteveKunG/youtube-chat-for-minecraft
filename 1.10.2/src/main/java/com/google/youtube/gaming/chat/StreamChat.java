/**
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.youtube.gaming.chat;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/**
 * Main entry point for YouTube Chat. Provides the chat service API to other mods, e.g.
 *
 * YouTubeChatService youTubeChatService = YouTubeChat.getService();
 */
@Mod(modid = StreamChat.MODID, name = StreamChat.NAME, version = StreamChat.VERSION, clientSideOnly = true, guiFactory = StreamChat.GUI_FACTORY)
public class StreamChat
{
    public static final String MODID = "stream_chat";
    public static final String NAME = "Stream Chat";
    public static final String VERSION = "1.3.1-1.10.2";
    public static final String GUI_FACTORY = "com.google.youtube.gaming.chat.ConfigGuiFactory";
    private static StreamChatService service;
    public static final JsonUtil json = new JsonUtil();
    public static GuiRightStreamChat rightStreamGui;
    private static boolean onDisconnected;
    private static boolean onConnected;
    private int ticks = 40;

    public static synchronized StreamChatService getService()
    {
        if (service == null)
        {
            service = new ChatService();
        }
        return service;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        StreamChat.initModInfo(event.getModMetadata());
        ConfigManager.initialize(event.getSuggestedConfigurationFile());
        ChatService service = (ChatService) StreamChat.getService();
        ClientCommandHandler.instance.registerCommand(new CommandYouTubeChat(service));
        ClientCommandHandler.instance.registerCommand(new CommandClearRightChat());
        ClientCommandHandler.instance.registerCommand(new CommandChatAction(service));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        StreamChat.rightStreamGui = new GuiRightStreamChat(Minecraft.getMinecraft());
        onConnected = true;
    }

    @SubscribeEvent
    public void onClientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        onDisconnected = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (CommandYouTubeChat.isReceivedChat)
        {
            if (onConnected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    StreamChat.service.subscribe(StreamChatReceiver.getInstance());
                    onConnected = false;
                    this.ticks = 40;
                }
            }
            if (onDisconnected && this.ticks > 0)
            {
                this.ticks--;

                if (this.ticks == 0)
                {
                    StreamChat.service.unsubscribe(StreamChatReceiver.getInstance());
                    onDisconnected = false;
                    this.ticks = 40;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPreInfoRender(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.translate(width / 2, height - 48, 0.0F);
            StreamChat.rightStreamGui.drawChat(Minecraft.getMinecraft().ingameGUI.getUpdateCounter());
            GlStateManager.popMatrix();
        }
    }

    private static void initModInfo(ModMetadata info)
    {
        info.autogenerated = false;
        info.modId = StreamChat.MODID;
        info.name = StreamChat.NAME;
        info.description = "Enables interaction with Live Stream Chat.";
        info.version = StreamChat.VERSION;
        info.authorList = Arrays.asList("jimrogers");
        info.credits = "Credit to PeregrineZ for implemented Example Features";
    }
}