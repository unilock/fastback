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
import net.pcal.fastback.config.GitConfigKey;
import net.pcal.fastback.logging.UserLogger;
import net.pcal.fastback.logging.UserMessage;
import net.pcal.fastback.repo.Repo;
import net.pcal.fastback.retention.RetentionPolicy;
import net.pcal.fastback.retention.RetentionPolicyCodec;
import net.pcal.fastback.retention.RetentionPolicyType;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static net.pcal.fastback.config.FastbackConfigKey.*;
import static net.pcal.fastback.config.OtherConfigKey.REMOTE_PUSH_URL;
import static net.pcal.fastback.logging.UserLogger.ulog;
import static net.pcal.fastback.logging.UserMessage.raw;
import static net.pcal.fastback.mod.Mod.mod;
import static net.pcal.fastback.repo.RepoFactory.rf;
import static net.pcal.fastback.utils.EnvironmentUtils.isNativeOk;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.sizeOfDirectory;

// TODO move this to Repo.doInfo
enum InfoCommand implements Command {

    INSTANCE;

    @Override
    public void execute(ICommandSender sender, String[] args) {
        requireNonNull(sender);
        try (final UserLogger ulog = ulog(sender)) {
            try {
                ulog.message(UserMessage.localized("fastback.chat.info-header"));
                ulog.message(UserMessage.localized("fastback.chat.info-fastback-version", mod().getModVersion()));
                if (!rf().isGitRepo(mod().getWorldDirectory())) {
                    // If they haven't yet run 'backup init', make sure they've installed native.
                    if (!isNativeOk(true, ulog, true)) return;
                } else {
                    try (final Repo repo = rf().load(mod().getWorldDirectory())) {
                        final GitConfig conf = repo.getConfig();
                        if (!isNativeOk(conf, ulog, true)) return;
                        ulog.message(UserMessage.localized("fastback.chat.info-uuid", repo.getWorldId().toString()));
                        // FIXME? this could be implemented more efficiently
                        final long backupSize = sizeOfDirectory(repo.getDirectory());
                        final long worldSize = sizeOfDirectory(repo.getWorkTree()) - backupSize;
                        ulog.message(UserMessage.localized("fastback.chat.info-world-size", byteCountToDisplaySize(worldSize)));
                        ulog.message(UserMessage.localized("fastback.chat.info-backup-size", byteCountToDisplaySize(backupSize)));

                        show(IS_BACKUP_ENABLED, conf::getBoolean, ulog);
                        show(REMOTE_PUSH_URL, conf::getString, ulog);
                        show(RESTORE_DIRECTORY, conf::getString, ulog);
                        show(AUTOBACK_WAIT_MINUTES, conf::getInt, ulog);
                        show(IS_MODS_BACKUP_ENABLED, conf::getBoolean, ulog);
                        show(BROADCAST_ENABLED, conf::getBoolean, ulog);
                        show(BROADCAST_MESSAGE, conf::getString, ulog);

                        final SchedulableAction shutdownAction = SchedulableAction.forConfigValue(conf.getString(SHUTDOWN_ACTION));
                        ulog.message(UserMessage.localized("fastback.chat.info-shutdown-action", getActionDisplay(shutdownAction)));
                        final SchedulableAction autobackAction = SchedulableAction.forConfigValue(conf.getString(AUTOBACK_ACTION));
                        ulog.message(UserMessage.localized("fastback.chat.info-autoback-action", getActionDisplay(autobackAction)));

                        showRetentionPolicy(ulog,
                            conf.getString(LOCAL_RETENTION_POLICY),
                            "fastback.chat.retention-policy-set",
                            "fastback.chat.retention-policy-none"
                        );
                        showRetentionPolicy(ulog,
                            conf.getString(REMOTE_RETENTION_POLICY),
                            "fastback.chat.remote-retention-policy-set",
                            "fastback.chat.remote-retention-policy-none"
                        );
                    }
                }
            } catch (final Exception e) {
                ulog.internalError(e);
            }
        }
    }

    private static void show(GitConfigKey key, Function<GitConfigKey, Object> valueFn, UserLogger ulog) {
        ulog.message(raw(key.getDisplayName() + " = " + valueFn.apply(key)));
    }

    private static String getActionDisplay(SchedulableAction action) {
        return action == null ? SchedulableAction.NONE.getArgumentName() : action.getArgumentName();
    }

    private static void showRetentionPolicy(UserLogger log, String encodedPolicy, String setKey, String noneKey) {
        if (encodedPolicy == null) {
            log.message(UserMessage.localized(noneKey));
        } else {
            final RetentionPolicy policy = RetentionPolicyCodec.INSTANCE.
                    decodePolicy(RetentionPolicyType.getAvailable(), encodedPolicy);
            if (policy == null) {
                log.message(UserMessage.localized(noneKey));
            } else {
                log.message(UserMessage.localized(setKey));
                log.message(policy.getDescription());
            }
        }
    }
}
