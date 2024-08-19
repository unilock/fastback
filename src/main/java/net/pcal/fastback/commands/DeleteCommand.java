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
import net.pcal.fastback.repo.SnapshotId;

import java.util.List;

import static net.pcal.fastback.commands.Commands.*;
import static net.pcal.fastback.logging.UserLogger.ulog;
import static net.pcal.fastback.utils.Executor.ExecutionLock.WRITE;

enum DeleteCommand implements Command {

    INSTANCE;

    @Override
    public void execute(ICommandSender sender, String[] args) {
        try (final UserLogger log = ulog(sender)) {
            if (args.length == 3) {
                gitOp(WRITE, log, repo -> {
                    final String snapshotName = args[2];
                    final SnapshotId sid = repo.createSnapshotId(snapshotName);
                    final String branchName = sid.getBranchName();
                    repo.deleteLocalBranches(List.of(branchName));
                    log.message(UserMessage.localized("fastback.chat.delete-done", snapshotName));
                });
            } else {
                missingArgument("snapshot", log);
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
