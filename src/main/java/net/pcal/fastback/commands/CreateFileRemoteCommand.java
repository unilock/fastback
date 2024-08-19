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
import net.pcal.fastback.config.GitConfig;
import net.pcal.fastback.logging.UserLogger;
import net.pcal.fastback.logging.UserMessage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.StoredConfig;

import java.nio.file.Path;

import static net.pcal.fastback.commands.Commands.gitOp;
import static net.pcal.fastback.commands.Commands.missingArgument;
import static net.pcal.fastback.config.FastbackConfigKey.IS_FILE_REMOTE_BARE;
import static net.pcal.fastback.config.OtherConfigKey.REMOTE_PUSH_URL;
import static net.pcal.fastback.logging.UserMessage.UserMessageStyle.ERROR;
import static net.pcal.fastback.logging.UserMessage.styledLocalized;
import static net.pcal.fastback.utils.Executor.ExecutionLock.NONE;
import static net.pcal.fastback.utils.FileUtils.mkdirs;

enum CreateFileRemoteCommand implements Command {

    INSTANCE;

    @Override
    public void execute(ICommandSender sender, String[] args) {
        try (final UserLogger ulog = UserLogger.ulog(sender)) {
            if (args.length == 3) {
                gitOp(NONE, ulog, repo -> {
                    final String targetPath = args[2];
                    final Path fupHome = Path.of(targetPath);
                    if (fupHome.toFile().exists()) {
                        ulog.message(styledLocalized("fastback.chat.create-file-remote-dir-exists", ERROR, fupHome.toString()));
                        return;
                    }
                    mkdirs(fupHome);
                    GitConfig conf = repo.getConfig();
                    try (Git targetGit = Git.init().setBare(conf.getBoolean(IS_FILE_REMOTE_BARE)).setDirectory(fupHome.toFile()).call()) {
                        final StoredConfig targetGitc = targetGit.getRepository().getConfig();
                        targetGitc.setInt("pack", null, "window", 0);
                        targetGitc.setInt("core", null, "bigFileThreshold", 1);
                        targetGitc.save();
                    }
                    final String targetUrl = "file://" + fupHome.toAbsolutePath();
                    repo.getConfig().updater().set(REMOTE_PUSH_URL, targetUrl).save();
                    ulog.message(UserMessage.localized("fastback.chat.create-file-remote-created", targetPath, targetUrl));
                });
            } else {
                missingArgument("file-path", ulog);
            }
        }
    }
}
