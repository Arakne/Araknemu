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

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.common.account.LivingAccount;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.common.account.banishment.BanIpRule;
import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.ConcatRestOfArgumentsHandler;
import fr.quatrevieux.araknemu.game.admin.executor.argument.type.SubArguments;
import fr.quatrevieux.araknemu.game.admin.executor.argument.type.SubArgumentsCommandTrait;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import inet.ipaddr.IPAddressString;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

/**
 * Bandle banned IP addresses
 */
public final class Banip extends AbstractCommand<Banip.Arguments> implements SubArgumentsCommandTrait<Banip.Arguments> {
    private final BanIpService<GameAccount> service;

    public Banip(BanIpService<GameAccount> service) {
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(formatter -> formatter
                .description("Handle banned IP addresses")
                .synopsis("banip [add|remove|list|check] ARGUMENTS")

                .option("add IP_ADDRESS [for DURATION|forever] CAUSE", "Add a new banned IP address. The IP address can be an IPv4 or IPv6 subnetwork mask.")
                .option("remove IP_ADDRESS", "Remove a banned IP address.")
                .option("list", "Dump list of banned ip rules.")
                .option("check IP_ADDRESS", "Check if the IP address is banned, and the ban rule.")

                .example("*banip add 11.54.47.21 forever my ban message", "Ban the IP address 11.54.47.21 forever")
                .example("*banip add 11.54.47.21 for 2h my ban message", "Ban the IP address 11.54.47.21 for 2 hours")
                .example("*banip add 11.54.0.0/16 for 2h my ban message", "Ban with a subnetwork mask")
                .example("*banip remove 11.54.52.32", "Remove the banned IP address 11.54.52.32")
                .example("*banip list", "List all banned IP addresses")
                .example("*banip check 11.54.52.32", "Check if 11.54.52.32 is banned")
            )
            .requires(Permission.SUPER_ADMIN)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "banip";
    }

    /**
     * List all ban ip rules
     */
    private void list(AdminPerformer performer) {
        final Collection<BanIpRule<GameAccount>> rules = service.rules();

        if (rules.isEmpty()) {
            performer.success("The ban ip table is empty");
            return;
        }

        performer.success("List of ban ip rules :");
        rules.stream().map(this::render).forEach(performer::info);
    }

    /**
     * Check if an IP address is banned
     */
    private void check(AdminPerformer performer, Arguments.CheckArguments arguments) {
        final Optional<BanIpRule<GameAccount>> rule = service.matching(arguments.ipAddress());

        if (!rule.isPresent()) {
            performer.error(
                "The IP address {} is not banned. {}",
                arguments.ipAddress().toNormalizedString(),
                Link.Type.WRITE.create("*banip add " + arguments.ipAddress().toNormalizedString() + " for").text("add")
            );
            return;
        }

        performer.success("The IP address {} is banned.", arguments.ipAddress().toNormalizedString());
        performer.info("Rule : {}", render(rule.get()));
    }

    /**
     * Remove a banned ip address rule
     */
    private void remove(AdminPerformer performer, Arguments.RemoveArguments arguments) {
        service.disable(arguments.ipAddress());
        performer.success("The IP address {} has been unbanned.", arguments.ipAddress().toNormalizedString());
    }

    /**
     * Add a ban ip address rule
     */
    private void add(AdminPerformer performer, Arguments.AddArguments arguments) throws CommandException {
        if (performer.account()
            .flatMap(GameAccount::session)
            .map(session -> new IPAddressString(session.channel().address().getAddress().getHostAddress()))
            .filter(arguments.ipAddress()::contains)
            .isPresent()) {
            error("Cannot ban your own IP address");
        }

        final BanIpService<GameAccount>.RuleBuilder builder = service.newRule(arguments.ipAddress());

        arguments.duration().ifPresent(builder::duration);
        builder.cause(arguments.cause());
        performer.account().ifPresent(builder::banisher);
        builder.apply();

        performer.success("The IP address {} has been banned.", arguments.ipAddress().toNormalizedString());
    }

    /**
     * Render a ban ip rule
     */
    private String render(BanIpRule<GameAccount> rule) {
        return rule.ipAddress().toNormalizedString() + " " +
            rule.expiresAt().map(instant -> "until " + instant).orElse("forever") + " " +
            "(by " + rule.banisher().map(LivingAccount::pseudo).orElse("system") + ") - " +
            rule.cause() + " " +
            Link.Type.EXECUTE.create("*banip remove " + rule.ipAddress().toNormalizedString()).text("remove")
        ;
    }

    @SuppressWarnings("initialization.field.uninitialized")
    public static final class Arguments implements SubArguments<Banip> {
        @Argument(required = true, metaVar = "ACTION")
        @SubCommands({
            @SubCommand(name = "add", impl = AddArguments.class),
            @SubCommand(name = "remove", impl = RemoveArguments.class),
            @SubCommand(name = "check", impl = CheckArguments.class),
            @SubCommand(name = "list", impl = ListArguments.class),
        })
        private SubArguments<Banip> sub;

        @Override
        public void execute(AdminPerformer performer, Banip command) throws AdminException {
            sub.execute(performer, command);
        }

        @SuppressWarnings("initialization.field.uninitialized")
        public abstract static class AbstractIpSubArguments implements SubArguments<Banip> {
            @Argument(index = 0, required = true, metaVar = "IP_ADDRESS")
            private IPAddressString ipAddress;

            public final IPAddressString ipAddress() {
                return ipAddress;
            }
        }

        @SuppressWarnings("initialization.field.uninitialized")
        public static final class ListArguments implements SubArguments<Banip> {
            @Override
            public void execute(AdminPerformer performer, Banip command) {
                command.list(performer);
            }
        }

        @SuppressWarnings("initialization.field.uninitialized")
        public static final class CheckArguments extends AbstractIpSubArguments {
            @Override
            public void execute(AdminPerformer performer, Banip command) throws CommandException {
                command.check(performer, this);
            }
        }

        @SuppressWarnings("initialization.field.uninitialized")
        public static final class RemoveArguments extends AbstractIpSubArguments {
            @Override
            public void execute(AdminPerformer performer, Banip command) throws CommandException {
                command.remove(performer, this);
            }
        }

        @SuppressWarnings("initialization.field.uninitialized")
        public static final class AddArguments extends AbstractIpSubArguments {
            // @todo do not use sub command here
            @Argument(index = 1, required = true, handler = SubCommandHandler.class, metaVar = "DURATION")
            @SubCommands({
                @SubCommand(name = "for", impl = For.class),
                @SubCommand(name = "forever", impl = Forever.class),
            })
            private DurationContainer duration;

            public Optional<Duration> duration() {
                return duration.get();
            }

            public String cause() {
                return duration.cause();
            }

            @Override
            public void execute(AdminPerformer performer, Banip command) throws CommandException {
                command.add(performer, this);
            }

            interface DurationContainer {
                public Optional<Duration> get();

                public String cause();
            }

            @SuppressWarnings("initialization.field.uninitialized")
            public static final class For implements DurationContainer {
                @Argument(required = true, metaVar = "DURATION")
                private Duration duration;

                @Argument(index = 1, required = true, handler = ConcatRestOfArgumentsHandler.class, metaVar = "MESSAGE")
                private String cause;

                @Override
                public Optional<Duration> get() {
                    return Optional.of(duration);
                }

                @Override
                public String cause() {
                    return cause;
                }
            }

            @SuppressWarnings("initialization.field.uninitialized")
            public static final class Forever implements DurationContainer {
                @Argument(required = true, handler = ConcatRestOfArgumentsHandler.class, metaVar = "MESSAGE")
                private String cause;

                @Override
                public Optional<Duration> get() {
                    return Optional.empty();
                }

                @Override
                public String cause() {
                    return cause;
                }
            }
        }
    }
}
