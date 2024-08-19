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
import net.pcal.fastback.repo.RepoFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static net.pcal.fastback.logging.UserLogger.ulog;
import static net.pcal.fastback.mod.Mod.mod;
import static net.pcal.fastback.utils.Executor.ExecutionLock.NONE;
import static net.pcal.fastback.utils.Executor.executor;

/**
 * @author pcal
 * @since 0.15.0
 */
enum InitCommand implements Command {

    INSTANCE;

    @Override
    public void execute(ICommandSender sender, String[] args) {
        try (final UserLogger ulog = ulog(sender)) {
            executor().execute(NONE, ulog, () -> {
                    final Path worldSaveDir = mod().getWorldDirectory();
                    final RepoFactory rf = RepoFactory.rf();
                    try {
                        rf.doInit(worldSaveDir, ulog);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
        }
    }
}