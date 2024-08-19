/*
 * FastBack - Fast, incremental Minecraft backups powered by Git.
 * Copyright (C) 2022 pcal.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package net.pcal.fastback.commands;

import net.minecraft.command.ICommandSender;
import net.pcal.fastback.logging.UserLogger;
import net.pcal.fastback.logging.UserMessage;

import java.io.StringWriter;
import java.util.List;

import static net.pcal.fastback.commands.Commands.SUBCOMMANDS;
import static net.pcal.fastback.commands.Commands.getListOfStringsMatchingLastWord;
import static net.pcal.fastback.logging.UserLogger.ulog;
import static net.pcal.fastback.logging.UserMessage.UserMessageStyle.ERROR;
import static net.pcal.fastback.logging.UserMessage.styledLocalized;
import static net.pcal.fastback.mod.Mod.mod;
import static net.pcal.fastback.repo.RepoFactory.rf;

enum HelpCommand implements Command {

    INSTANCE;

    @Override
    public void execute(ICommandSender sender, String[] args) {
        try (final UserLogger ulog = ulog(sender)) {
            if (args.length == 1) {
                StringWriter subcommands = null;
                for (final String available : SUBCOMMANDS) {
                    if (subcommands == null) {
                        subcommands = new StringWriter();
                    } else {
                        subcommands.append(", ");
                    }
                    subcommands.append(available);
                }
                ulog.message(UserMessage.localized("fastback.help.subcommands", String.valueOf(subcommands)));
                if (!rf().isGitRepo(mod().getWorldDirectory())) {
                    ulog.message(UserMessage.localized("fastback.help.suggest-init"));
                }
            } else if (args.length == 2) {
                final String subcommand = args[1];
                for (String available : SUBCOMMANDS) {
                    if (subcommand.equals(available)) {
                        final String prefix = "/backup " + subcommand + ": ";
                        ulog.message(UserMessage.localized("fastback.help.command." + subcommand, prefix));
                        return;
                    }
                }
                ulog.message(styledLocalized("fastback.chat.invalid-input", ERROR, subcommand));
            }
        }
    }

    @Override
    public List<String> suggestions(ICommandSender sender, String[] args) {
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, SUBCOMMANDS);
        } else {
            return Command.super.suggestions(sender, args);
        }
    }
}
