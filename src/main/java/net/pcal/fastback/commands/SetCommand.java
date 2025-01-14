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

import java.util.List;

/**
 * Sets various configuration values.
 *
 * @author pcal
 * @since 0.13.0
 */
enum SetCommand implements Command {

    INSTANCE;

    // ======================================================================
    // Command implementation

    private static final String COMMAND_NAME = "set";

//    @Override
//    public void register(final LiteralArgumentBuilder<CommandSourceStack> root, PermissionsFactory<CommandSourceStack> pf) {
//        final LiteralArgumentBuilder<CommandSourceStack> sc = literal(COMMAND_NAME).
//                requires(subcommandPermission(COMMAND_NAME, pf)).
//                executes(cc -> missingArgument("key", cc));
//        registerBooleanConfigValue(IS_LOCK_CLEANUP_ENABLED, sc);
//        registerBooleanConfigValue(IS_BACKUP_ENABLED, sc);
//        registerBooleanConfigValue(IS_MODS_BACKUP_ENABLED, sc);
//        registerBooleanConfigValue(BROADCAST_ENABLED, sc);
//        registerStringConfigValue(BROADCAST_MESSAGE, "message", sc);
//        registerStringConfigValue(RESTORE_DIRECTORY, "full-directory-path", sc);
//        registerStringConfigValue(REMOTE_PUSH_URL, "url", sc);
//        registerIntegerConfigValue(AUTOBACK_WAIT_MINUTES, "minutes", sc);
//
//        {
//            final List<String> schedulableActions = new ArrayList<>();
//            for (final SchedulableAction sa : SchedulableAction.values()) {
//                schedulableActions.add(sa.getConfigValue());
//            }
//            registerSelectConfigValue(AUTOBACK_ACTION, schedulableActions, sc);
//            registerSelectConfigValue(SHUTDOWN_ACTION, schedulableActions, sc);
//        }
//
//        registerSetRetentionCommand(LOCAL_RETENTION_POLICY, sc);
//        registerSetRetentionCommand(REMOTE_RETENTION_POLICY, sc);
//
//        registerForceDebug(sc);
//        root.then(sc);
//    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        // TODO :(
    }

    @Override
    public List<String> suggestions(ICommandSender sender, String[] args) {
        // TODO :(
        return Command.super.suggestions(sender, args);
    }


    // ======================================================================
    // Boolean config values

//    private static void registerBooleanConfigValue(final GitConfigKey key, final LiteralArgumentBuilder<CommandSourceStack> setCommand) {
//        final LiteralArgumentBuilder<CommandSourceStack> builder = literal(key.getDisplayName());
//        builder.then(literal("true").executes(cc -> setBooleanConfigValue(key, true, cc)));
//        builder.then(literal("false").executes(cc -> setBooleanConfigValue(key, false, cc)));
//        setCommand.then(builder);
//    }
//
//    private static int setBooleanConfigValue(final GitConfigKey key, final boolean newValue, final CommandContext<CommandSourceStack> cc) {
//        try (UserLogger ulog = ulog(cc)) {
//            final Path worldSaveDir = mod().getWorldDirectory();
//            final RepoFactory rf = rf();
//            if (rf.isGitRepo(worldSaveDir)) {
//                try (Repo repo = rf.load(worldSaveDir)) {
//                    final GitConfig conf = repo.getConfig();
//                    boolean current = conf.getBoolean(key);
//                    if (current == newValue) {
//                        ulog.message(localized("fastback.chat.no-change"));
//                    } else {
//                        repo.getConfig().updater().set(key, newValue).save();
//                        ulog.message(raw(key.getDisplayName() + " = " + newValue));
//                    }
//                } catch (Exception e) {
//                    ulog.internalError(e);
//                    return FAILURE;
//                }
//            }
//        }
//        return SUCCESS;
//    }

    // ======================================================================
    // Integer config values

//    private static void registerIntegerConfigValue(final GitConfigKey key, final String argName, final LiteralArgumentBuilder<CommandSourceStack> setCommand) {
//        final LiteralArgumentBuilder<CommandSourceStack> builder = literal(key.getDisplayName());
//        builder.then(argument(argName, IntegerArgumentType.integer()).
//                executes(cc -> setIntegerConfigValue(key, argName, cc)));
//        setCommand.then(builder);
//    }
//
//    private static int setIntegerConfigValue(final GitConfigKey key, final String argName, final CommandContext<CommandSourceStack> cc) {
//        try (UserLogger ulog = ulog(cc)) {
//            final Path worldSaveDir = mod().getWorldDirectory();
//            final RepoFactory rf = rf();
//            if (rf.isGitRepo(worldSaveDir)) {
//                try (Repo repo = rf.load(worldSaveDir)) {
//                    final Integer newValue = cc.getArgument(argName, Integer.class);
//                    repo.getConfig().updater().set(key, newValue).save();
//                    ulog.message(raw(key.getDisplayName() + " = " + newValue));
//                } catch (Exception e) {
//                    ulog.internalError(e);
//                    return FAILURE;
//                }
//            }
//        }
//        return SUCCESS;
//    }

    // ======================================================================
    // String config values

//    private static void registerStringConfigValue(final GitConfigKey key, final String argName, final LiteralArgumentBuilder<CommandSourceStack> setCommand) {
//        final LiteralArgumentBuilder<CommandSourceStack> builder = literal(key.getDisplayName());
//        builder.then(argument(argName, StringArgumentType.greedyString()).
//                executes(cc -> setStringConfigValue(key, argName, cc)));
//        setCommand.then(builder);
//    }
//
//    private static int setStringConfigValue(final GitConfigKey key, final String argName, final CommandContext<CommandSourceStack> cc) {
//        try (UserLogger ulog = ulog(cc)) {
//            final Path worldSaveDir = mod().getWorldDirectory();
//            final RepoFactory rf = rf();
//            if (rf.isGitRepo(worldSaveDir)) {
//                try (Repo repo = rf.load(worldSaveDir)) {
//                    final String newValue = cc.getArgument(argName, String.class);
//                    repo.getConfig().updater().set(key, newValue).save();
//                    ulog.message(raw(key.getDisplayName() + " = " + newValue));
//                } catch (Exception e) {
//                    ulog.internalError(e);
//                    return FAILURE;
//                }
//            }
//        }
//        return SUCCESS;
//    }

    // ======================================================================
    // Selection config values

//    private static void registerSelectConfigValue(GitConfigKey key, List<String> selections, final LiteralArgumentBuilder<CommandSourceStack> setCommand) {
//        final LiteralArgumentBuilder<CommandSourceStack> builder = literal(key.getDisplayName());
//        for (final String selection : selections) {
//            builder.then(literal(selection).executes(cc -> setSelectionConfigValue(key, selection, cc)));
//        }
//        setCommand.then(builder);
//    }
//
//    private static int setSelectionConfigValue(final GitConfigKey key, final String newValue, final CommandContext<CommandSourceStack> cc) {
//        try (UserLogger ulog = ulog(cc)) {
//            final Path worldSaveDir = mod().getWorldDirectory();
//            if (rf().isGitRepo(worldSaveDir)) {
//                try (final Repo repo = rf().load(worldSaveDir)) {
//                    repo.getConfig().updater().set(key, newValue).save();
//                    ulog.message(raw(key.getDisplayName() + " = " + newValue));
//                } catch (Exception e) {
//                    ulog.internalError(e);
//                    return FAILURE;
//                }
//            }
//        }
//        return SUCCESS;
//    }

    // ======================================================================
    // force-debug

//    private static final String FORCE_DEBUG_SETTING = "force-debug-enabled";
//
//    private static void registerForceDebug(final LiteralArgumentBuilder<CommandSourceStack> setCommand) {
//        final LiteralArgumentBuilder<CommandSourceStack> debug = literal(FORCE_DEBUG_SETTING);
//        debug.then(literal("true").executes(cc -> setForceDebug(cc, true)));
//        debug.then(literal("false").executes(cc -> setForceDebug(cc, false)));
//        setCommand.then(debug);
//    }
//
//    private static int setForceDebug(final CommandContext<CommandSourceStack> cc, boolean value) {
//        syslog().setForceDebugEnabled(value);
//        try (final UserLogger ulog = ulog(cc)) {
//            ulog.message(raw("force-debug-enabled = " + value));
//        }
//        return SUCCESS;
//    }


    // ======================================================================
    // Retention policy commands

    /**
     * Register a 'set retention' command that tab completes with all the policies and the policy arguments.
     * Broken out as a helper methods so this logic can be shared by set-retention and set-remote-retention.
     * <p>
     * FIXME? The command parsing here could be more user-friendly.  Not really clear how to implement
     * argument defaults.  Also a lot of noise from bugs like this: https://bugs.mojang.com/browse/MC-165562
     * Just generally not sure how to beat brigadier into submission here.
     */
//    private static void registerSetRetentionCommand(final FastbackConfigKey key,
//                                                    final LiteralArgumentBuilder<CommandSourceStack> argb) {
//        final LiteralArgumentBuilder<CommandSourceStack> retainCommand = literal(key.getSettingName());
//        for (final RetentionPolicyType rpt : RetentionPolicyType.getAvailable()) {
//            final LiteralArgumentBuilder<CommandSourceStack> policyCommand = literal(rpt.getCommandName());
//            policyCommand.executes(cc -> setRetentionPolicy(cc, rpt, key));
//            if (rpt.getParameters() != null) {
//                for (RetentionPolicyType.Parameter<?> param : rpt.getParameters()) {
//                    policyCommand.then(argument(param.name(), param.type()).
//                            executes(cc -> setRetentionPolicy(cc, rpt, key)));
//                }
//            }
//            retainCommand.then(policyCommand);
//        }
//        argb.then(retainCommand);
//    }


    /**
     * Does the work to encode a policy configuration and set it in git configuration.
     * Broken out as a helper methods so this logic can be shared by set-retention and set-remote-retention.
     * <p>
     * TODO this should probably move to Repo.
     */
//    public static int setRetentionPolicy(final CommandContext<CommandSourceStack> cc,
//                                         final RetentionPolicyType rpt,
//                                         final FastbackConfigKey confKey) {
//        final UserLogger ulog = ulog(cc);
//        final Path worldSaveDir = mod().getWorldDirectory();
//        try (final Repo repo = rf().load(worldSaveDir)) {
//            final Map<String, String> config = new HashMap<>();
//            for (final RetentionPolicyType.Parameter<?> p : rpt.getParameters()) {
//                final Object val = getArgumentNicely(p.name(), p.clazz(), cc, ulog);
//                if (val == null) return FAILURE;
//                config.put(p.name(), String.valueOf(val));
//            }
//            final String encodedPolicy = RetentionPolicyCodec.INSTANCE.encodePolicy(rpt, config);
//            final RetentionPolicy rp =
//                    RetentionPolicyCodec.INSTANCE.decodePolicy(RetentionPolicyType.getAvailable(), encodedPolicy);
//            if (rp == null) {
//                syslog().error("Failed to decode policy " + encodedPolicy, new Exception());
//                return FAILURE;
//            }
//            final GitConfig conf = repo.getConfig();
//            conf.updater().set(confKey, encodedPolicy).save();
//            ulog.message(localized("fastback.chat.retention-policy-set"));
//            ulog.message(rp.getDescription());
//            return SUCCESS;
//        } catch (Exception e) {
//            syslog().error("Failed to set retention policy", e);
//            return FAILURE;
//        }
//    }
}
