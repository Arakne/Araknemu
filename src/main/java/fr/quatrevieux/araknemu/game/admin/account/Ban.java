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
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.CustomEnumOptionHandler;
import org.kohsuke.args4j.Argument;

import java.time.Duration;
import java.util.List;

/**
 * Handle banishment for an account
 */
public final class Ban extends AbstractCommand<Ban.Arguments> {
    private final GameAccount account;
    private final BanishmentService<GameAccount> service;

    public Ban(GameAccount account, BanishmentService<GameAccount> service) {
        this.account = account;
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Ban an account")
            .help(
                formatter -> formatter
                    .synopsis("[context] ban [for|list|unban] [arguments]")

                    .options("for [duration] [cause]", "Ban the account for the given duration.\nThe duration is in format [days]dT[hours]h[minutes]m[seconds]s\nNote: You cannot ban a game master account.")
                    .options("list", "List all banishment entries for the account")
                    .options("unban", "Remove current banishment for the account")

                    .example("${account:John} ban list", "Display all ban entries of the 'John' account")
                    .example("${account:John} ban for 5d", "Ban 'John' for 5 days")
                    .example("${player:Alan} ban for 10m", "Ban 'Alan' account for 10 minutes")
                    .example("${player:Alan} unban", "Unban 'Alan' account")
            )
            .requires(Permission.MANAGE_ACCOUNT)
        ;
    }

    @Override
    public String name() {
        return "ban";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        switch (arguments.action) {
            case FOR:
                banFor(performer, arguments);
                break;

            case LIST:
                list(performer);
                break;

            case UNBAN:
                unban(performer);
                break;
        }
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
    private void banFor(AdminPerformer performer, Arguments arguments) throws CommandException {
        checkCanBan(performer);

        if (arguments.duration == null) {
            throw new CommandException(name(), "Missing the duration");
        }

        // @todo handle by Arguments
        if (arguments.cause == null || arguments.cause.isEmpty()) {
            throw new CommandException(name(), "Missing the cause");
        }

        final BanEntry<GameAccount> entry = performer.account().isPresent()
            ? service.ban(account, arguments.duration, arguments.cause(), performer.account().get())
            : service.ban(account, arguments.duration, arguments.cause())
        ;

        performer.success("The account {} has been banned until {}", account.pseudo(), entry.end());
    }

    /**
     * Check if the account can be banned
     */
    private void checkCanBan(AdminPerformer performer) throws CommandException {
        if (performer.account().filter(performerAccount -> performerAccount.id() == account.id()).isPresent()) {
            throw new CommandException(name(), "Cannot ban yourself");
        }

        if (account.isMaster()) {
            throw new CommandException(name(), "Cannot ban a game master");
        }
    }

    @Override
    public Arguments createArguments() {
        return new Arguments();
    }

    public static enum Action {
        FOR,
        LIST,
        UNBAN,
    }

    public static final class Arguments {
        @Argument(required = true, handler = CustomEnumOptionHandler.class)
        private Action action;

        @Argument(index = 1)
        private Duration duration;

        @Argument(multiValued = true, index = 2)
        private List<String> cause;

        public String cause() {
            return String.join(" ", cause);
        }
    }
}
