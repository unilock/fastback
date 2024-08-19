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

import java.util.List;

import static net.pcal.fastback.commands.Commands.*;
import static net.pcal.fastback.utils.Executor.ExecutionLock.NONE;

enum RestoreCommand implements Command {

    INSTANCE;

    @Override
    public void execute(ICommandSender sender, String[] args) {
        try (final UserLogger ulog = UserLogger.ulog(sender)) {
            if (args.length == 3) {
                gitOp(NONE, ulog, repo -> {
                    final String snapshotName = args[2];
                    repo.doRestoreLocalSnapshot(snapshotName, ulog);
                });
            } else {
                missingArgument("snapshot", ulog);
            }
        }
    }

    @Override
    public List<String> suggestions(ICommandSender sender, String[] args) {
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, SnapshotNameSuggestions.local().getSuggestions(sender));
        } else {
            return Command.super.suggestions(sender, args);
        }
    }
}
