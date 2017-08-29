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

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

/**
 * An command that will clear chat messages on right side on the screen. Usage:
 *
 * /ytcclear
 * @author SteveKunG
 */
public class CommandClearRightChat extends ClientCommandBase
{
    @Override
    public String getCommandName()
    {
        return "ytcclear";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/ytcclear";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        StreamChat.rightStreamGui.clearChatMessages();
        ModLogger.printYTMessage(StreamChat.json.text("Clear Stream Chat message (Right Side)").setChatStyle(StreamChat.json.white()), ConfigManager.getInstance().getRightSideChat());
    }
}