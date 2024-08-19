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

import java.util.Collection;

import static net.pcal.fastback.commands.Commands.gitOp;
import static net.pcal.fastback.logging.UserLogger.ulog;
import static net.pcal.fastback.utils.Executor.ExecutionLock.WRITE;

/**
 * Command to prune all snapshots that are not to be retained per the retention policy.
 *
 * @author pcal
 * @since 0.2.0
 */
enum PruneCommand implements Command {

    INSTANCE;

    @Override
    public void execute(ICommandSender sender, String[] args) {
        try (final UserLogger ulog = ulog(sender)) {
            gitOp(WRITE, ulog, repo -> {
                final Collection<SnapshotId> pruned = repo.doLocalPrune(ulog);
                if (pruned != null) {
                    ulog.message(UserMessage.localized("fastback.chat.prune-done", pruned.size()));
                    if (!pruned.isEmpty()) ulog.message(UserMessage.localized("fastback.chat.prune-suggest-gc"));
                }
            });
        }
    }
}
