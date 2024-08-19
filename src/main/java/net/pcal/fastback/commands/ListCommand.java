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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.pcal.fastback.commands.Commands.gitOp;
import static net.pcal.fastback.mod.Mod.mod;
import static net.pcal.fastback.repo.RepoFactory.rf;
import static net.pcal.fastback.utils.Executor.ExecutionLock.NONE;

enum ListCommand implements Command {

    INSTANCE;

    @Override
    public void execute(ICommandSender sender, String[] args) {
        try (final UserLogger ulog = UserLogger.ulog(sender)) {
            if (!rf().doInitCheck(mod().getWorldDirectory(), ulog)) return;
            gitOp(NONE, ulog, repo -> {
                final List<SnapshotId> snapshots = new ArrayList<>(repo.getLocalSnapshots());
                Collections.sort(snapshots);
                for (final SnapshotId sid : snapshots) {
                    ulog.message(UserMessage.raw(sid.getShortName()));
                }
            });
        }
    }

}
