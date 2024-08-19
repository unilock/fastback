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

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.pcal.fastback.config.GitConfig;
import net.pcal.fastback.logging.UserLogger;
import net.pcal.fastback.repo.Repo;
import net.pcal.fastback.repo.RepoFactory;
import net.pcal.fastback.utils.Executor.ExecutionLock;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.command.CommandBase.doesStringStartWith;
import static net.pcal.fastback.config.FastbackConfigKey.IS_BACKUP_ENABLED;
import static net.pcal.fastback.logging.SystemLogger.syslog;
import static net.pcal.fastback.logging.UserMessage.UserMessageStyle.ERROR;
import static net.pcal.fastback.logging.UserMessage.styledLocalized;
import static net.pcal.fastback.mod.Mod.mod;
import static net.pcal.fastback.utils.EnvironmentUtils.isNativeOk;
import static net.pcal.fastback.utils.Executor.executor;

public class Commands {

    static final int FAILURE = 0;
    static final int SUCCESS = 1;

    static final List<String> SUBCOMMANDS = List.of(
        "init",
        "local",
        "full",
        "info",
        "restore",
        "create-file-remote",
        "prune",
        "delete",
        "gc",
        "list",
        "push",
        "remote-list",
        "remote-delete",
        "remote-prune",
        "remote-restore",
        "set",
        "help"
    );

    public static ICommand createBackupCommand() {

        return new CommandBase() {
            @Override
            public String getCommandName() {
                return "backup";
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "FIXME";
            }

            @Override
            public void processCommand(ICommandSender sender, String[] args) {
                Command command = pick(args[0]);
                if (command == null) {
                    sender.addChatMessage(new ChatComponentText("unknown subcommand"));
                } else {
                    command.execute(sender, args);
                }
            }

            @Override
            public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
                Command command = pick(args[0]);
                if (command == null) {
                    return Commands.getListOfStringsMatchingLastWord(args, SUBCOMMANDS);
                } else {
                    return command.suggestions(sender, args);
                }
            }
        };

    }

    static Command pick(String name) {
        return switch (name) {
            case "init" -> InitCommand.INSTANCE;
            case "local" -> LocalCommand.INSTANCE;
            case "full" -> FullCommand.INSTANCE;
            case "info" -> InfoCommand.INSTANCE;

            case "restore" -> RestoreCommand.INSTANCE;
            case "create-file-remote" -> CreateFileRemoteCommand.INSTANCE;

            case "prune" -> PruneCommand.INSTANCE;
            case "delete" -> DeleteCommand.INSTANCE;
            case "gc" -> GcCommand.INSTANCE;
            case "list" -> ListCommand.INSTANCE;
            case "push" -> PushCommand.INSTANCE;

            case "remote-list" -> RemoteListCommand.INSTANCE;
            case "remote-delete" -> RemoteDeleteCommand.INSTANCE;
            case "remote-prune" -> RemotePruneCommand.INSTANCE;
            case "remote-restore" -> RemoteRestoreCommand.INSTANCE;

            case "set" -> SetCommand.INSTANCE;

            case "help" -> HelpCommand.INSTANCE;

            default -> null;
        };
    }

    static int missingArgument(final String argName, final ICommandSender cc) {
        return missingArgument(argName, UserLogger.ulog(cc));
    }

    static int missingArgument(final String argName, final UserLogger log) {
        log.message(styledLocalized("fastback.chat.missing-argument", ERROR, argName));
        return FAILURE;
    }

    static List<String> getListOfStringsMatchingLastWord(String[] args, List<String> possibilities) {
        String s1 = args[args.length - 1];
        ArrayList<String> list = new ArrayList<>();

        for (String s2 : possibilities) {
            if (doesStringStartWith(s1, s2)) {
                list.add(s2);
            }
        }

        return list;
    }

    interface GitOp {
        void execute(Repo repo) throws Exception;
    }

    static void gitOp(final ExecutionLock lock, final UserLogger ulog, final GitOp op) {
        try {
            executor().execute(lock, ulog, () -> {
                final Path worldSaveDir = mod().getWorldDirectory();
                final RepoFactory rf = RepoFactory.rf();
                if (!rf.isGitRepo(worldSaveDir)) { // FIXME this is not the right place for these checks
                    // If they haven't yet run 'backup init', make sure they've installed native.
                    if (!isNativeOk(true, ulog, true)) return;
                    ulog.message(styledLocalized("fastback.chat.not-enabled", ERROR));
                    return;
                }
                try (final Repo repo = rf.load(worldSaveDir)) {
                    final GitConfig repoConfig = repo.getConfig();
                    if (!isNativeOk(repoConfig, ulog, false)) return;
                    if (!repoConfig.getBoolean(IS_BACKUP_ENABLED)) {
                        ulog.message(styledLocalized("fastback.chat.not-enabled", ERROR));
                    } else {
                        op.execute(repo);
                    }
                } catch (Exception e) {
                    ulog.message(styledLocalized("fastback.chat.internal-error", ERROR));
                    syslog().error(e);
                } finally {
                    mod().clearHudText();
                }
            });
        } catch (Exception e) {
            ulog.internalError();
            syslog().error(e);
        }
    }
}
