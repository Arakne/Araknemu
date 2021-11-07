/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.common.account.LivingAccount;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.common.account.banishment.BanEntry;
import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.ConcatRestOfArgumentsHandler;
import fr.quatrevieux.araknemu.game.admin.executor.argument.type.SubArguments;
import fr.quatrevieux.araknemu.game.admin.executor.argument.type.SubArgumentsCommandTrait;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommands;

import java.time.Duration;
import java.util.List;

/**
 * Handle banishment for an account
 */
public final class Ban extends AbstractCommand<Ban.Arguments> implements SubArgumentsCommandTrait<Ban.Arguments> {
    private final GameAccount account;
    private final BanishmentService<GameAccount> service;

    public Ban(GameAccount account, BanishmentService<GameAccount> service) {
        this.account = account;
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(
                formatter -> formatter
                    .description("Ban an account")
                    .synopsis("[context] ban [for|list|unban] ARGUMENTS")

                    .option("for DURATION CAUSE", "Ban the account for the given duration.\nThe duration is in format [days]dT[hours]h[minutes]m[seconds]s\nNote: You cannot ban a game master account.")
                    .option("list", "List all banishment entries for the account")
                    .option("unban", "Remove current banishment for the account")

                    .example("#John ban list", "Display all ban entries of the 'John' account")
                    .example("#John ban for 5d", "Ban 'John' for 5 days")
                    .example("@Alan ban for 10m", "Ban 'Alan' account for 10 minutes")
                    .example("@Alan unban", "Unban 'Alan' account")
            )
            .requires(Permission.MANAGE_ACCOUNT)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "ban";
    }

    /**
     * Unban the account
     */
    private void unban(AdminPerformer performer) {
        service.unban(account);
        performer.success("The account {} has been unbaned", account.pseudo());
    }

    /**
     * List all ban entries
     */
    private void list(AdminPerformer performer) {
        final List<BanEntry<GameAccount>> entries = service.list(account);

        if (entries.isEmpty()) {
            performer.success("No ban entries found");
            return;
        }

        performer.success("List of ban entries for {} :", account.pseudo());

        for (BanEntry<GameAccount> entry : entries) {
            performer.info(
                "{} - {} (by {}) : {}{}",
                entry.start(),
                entry.end(),
                entry.banisher().map(LivingAccount::pseudo).orElse("system"),
                entry.cause(),
                entry.active() ? " <b>active</b>" : ""
            );
        }
    }

    /**
     * Ban the account for a given duration
     */
    private void banFor(AdminPerformer performer, Arguments.ForArguments arguments) throws CommandException {
        checkCanBan(performer);

        final BanEntry<GameAccount> entry = performer.account().isPresent()
            ? service.ban(account, arguments.duration, arguments.cause, performer.account().get())
            : service.ban(account, arguments.duration, arguments.cause)
        ;

        performer.success("The account {} has been banned until {}", account.pseudo(), entry.end());
    }

    /**
     * Check if the account can be banned
     */
    private void checkCanBan(AdminPerformer performer) throws CommandException {
        if (performer.account().filter(performerAccount -> performerAccount.id() == account.id()).isPresent()) {
            error("Cannot ban yourself");
        }

        if (account.isMaster()) {
            error("Cannot ban a game master");
        }
    }

    public static final class Arguments implements SubArguments<Ban> {
        @Argument(required = true, metaVar = "ACTION")
        @SubCommands({
            @SubCommand(name = "for", impl = ForArguments.class),
            @SubCommand(name = "list", impl = ListArguments.class),
            @SubCommand(name = "unban", impl = UnbanArguments.class),
        })
        private SubArguments<Ban> action;

        @Override
        public void execute(AdminPerformer performer, Ban command) throws AdminException {
            action.execute(performer, command);
        }

        public static final class ForArguments implements SubArguments<Ban> {
            @Argument(index = 0, required = true, metaVar = "DURATION")
            private Duration duration;

            @Argument(index = 1, required = true, metaVar = "CAUSE", handler = ConcatRestOfArgumentsHandler.class)
            private String cause;

            @Override
            public void execute(AdminPerformer performer, Ban command) throws AdminException {
                command.banFor(performer, this);
            }
        }

        public static final class ListArguments implements SubArguments<Ban> {
            @Override
            public void execute(AdminPerformer performer, Ban command) throws AdminException {
                command.list(performer);
            }
        }

        public static final class UnbanArguments implements SubArguments<Ban> {
            @Override
            public void execute(AdminPerformer performer, Ban command) throws AdminException {
                command.unban(performer);
            }
        }
    }
}
