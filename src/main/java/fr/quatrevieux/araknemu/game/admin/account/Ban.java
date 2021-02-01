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

import java.time.DateTimeException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handle banishment for an account
 */
public final class Ban extends AbstractCommand {
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
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        if (arguments.size() < 2) {
            throw new CommandException(name(), "Missing the action");
        }

        switch (arguments.get(1)) {
            case "for":
                banFor(performer, arguments);
                break;

            case "list":
                list(performer);
                break;

            case "unban":
                unban(performer);
                break;

            default:
                throw new CommandException(name(), "The action " + arguments.get(1) + " is not valid");
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
    private void banFor(AdminPerformer performer, List<String> arguments) throws CommandException {
        checkCanBan(performer);

        if (arguments.size() < 3) {
            throw new CommandException(name(), "Missing the duration");
        }

        final Duration duration = parseDuration(arguments.get(2));
        final BanEntry<GameAccount> entry = performer.account().isPresent()
            ? service.ban(account, duration, cause(arguments), performer.account().get())
            : service.ban(account, duration, cause(arguments))
        ;

        performer.success("The account {} has been banned until {}", account.pseudo(), entry.end());
    }

    /**
     * Extract the ban cause
     */
    private String cause(List<String> arguments) throws CommandException {
        if (arguments.size() < 4) {
            throw new CommandException(name(), "Missing cause");
        }

        return arguments.stream().skip(3).collect(Collectors.joining(" "));
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

    /**
     * Parse the duration
     * @todo refactor parsing with "arguments utils"
     */
    private Duration parseDuration(String argument) throws CommandException {
        String value = argument.toUpperCase();

        if (value.charAt(0) != 'P') {
            if (!value.contains("T") && !value.contains("D")) {
                value = "PT" + value;
            } else {
                value = "P" + value;
            }
        }

        try {
            return Duration.parse(value);
        } catch (DateTimeException e) {
            throw new CommandException(name(), "Invalid duration", e);
        }
    }
}
