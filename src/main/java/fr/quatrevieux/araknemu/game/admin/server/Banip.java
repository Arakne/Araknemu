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
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import inet.ipaddr.IPAddressString;

import java.time.DateTimeException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Bandle banned IP addresses
 */
public final class Banip extends AbstractCommand {
    private final BanIpService<GameAccount> service;

    public Banip(BanIpService<GameAccount> service) {
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Handle banned IP addresses")
            .help(formatter -> formatter
                .synopsis("banip [add|remove|list|check] [parameters]")
                .options("add [ip address] [for [duration]|forever] [cause]", "Add a new banned IP address. The IP address can be an IPv4 or IPv6 subnetwork mask.")
                .options("remove [ip address]", "Remove a banned IP address.")
                .options("list", "Dump list of banned ip rules.")
                .options("check [ip address]", "Check if the IP address is banned, and the ban rule.")
                .example("${server} banip add 11.54.47.21 forever my ban message", "Ban the IP address 11.54.47.21 forever")
                .example("${server} banip add 11.54.47.21 for 2h my ban message", "Ban the IP address 11.54.47.21 for 2 hours")
                .example("${server} banip add 11.54.0.0/16 for 2h my ban message", "Ban with a subnetwork mask")
                .example("${server} banip remove 11.54.52.32", "Remove the banned IP address 11.54.52.32")
                .example("${server} banip list", "List all banned IP addresses")
                .example("${server} check 11.54.52.32", "Check if 11.54.52.32 is banned")
            )
            .requires(Permission.SUPER_ADMIN)
        ;
    }

    @Override
    public String name() {
        return "banip";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws CommandException {
        if (arguments.size() < 2) {
            throw new CommandException(name(), "Missing the operation");
        }

        switch (arguments.get(1).toLowerCase()) {
            case "add":
                add(performer, arguments);
                break;

            case "remove":
                remove(performer, arguments);
                break;

            case "check":
                check(performer, arguments);
                break;

            case "list":
                list(performer);
                break;

            default:
                throw new CommandException(name(), "Invalid operation");
        }
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
    private void check(AdminPerformer performer, List<String> arguments) throws CommandException {
        if (arguments.size() < 3) {
            throw new CommandException(name(), "Missing the IP address");
        }

        final IPAddressString ipAddress = new IPAddressString(arguments.get(2));

        if (!ipAddress.isValid()) {
            throw new CommandException(name(), "Invalid IP address given");
        }

        final Optional<BanIpRule<GameAccount>> rule = service.matching(ipAddress);

        if (!rule.isPresent()) {
            performer.error(
                "The IP address {} is not banned. {}",
                ipAddress.toNormalizedString(),
                Link.Type.WRITE.create("${server} banip add " + ipAddress.toNormalizedString() + " for").text("add")
            );
            return;
        }

        performer.success("The IP address {} is banned.", ipAddress.toNormalizedString());
        performer.info("Rule : {}", render(rule.get()));
    }

    /**
     * Remove a banned ip address rule
     */
    private void remove(AdminPerformer performer, List<String> arguments) throws CommandException {
        if (arguments.size() < 3) {
            throw new CommandException(name(), "Missing the IP address");
        }

        final IPAddressString ipAddress = new IPAddressString(arguments.get(2));

        if (!ipAddress.isValid()) {
            throw new CommandException(name(), "Invalid IP address given");
        }

        service.disable(ipAddress);
        performer.success("The IP address {} has been unbanned.", ipAddress.toNormalizedString());
    }

    /**
     * Add a ban ip address rule
     */
    private void add(AdminPerformer performer, List<String> arguments) throws CommandException {
        if (arguments.size() < 3) {
            throw new CommandException(name(), "Missing the IP address");
        }

        final IPAddressString ipAddress = new IPAddressString(arguments.get(2));

        if (!ipAddress.isValid()) {
            throw new CommandException(name(), "Invalid IP address given");
        }

        if (performer.account()
            .flatMap(GameAccount::session)
            .map(session -> new IPAddressString(session.channel().address().getAddress().getHostAddress()))
            .filter(ipAddress::contains)
            .isPresent()) {
            throw new CommandException(name(), "Cannot ban your own IP address");
        }

        final BanIpService<GameAccount>.RuleBuilder builder = service.newRule(ipAddress);

        if (arguments.size() < 4) {
            throw new CommandException(name(), "Missing the duration");
        }

        final int causeOffset;

        switch (arguments.get(3)) {
            case "for":
                if (arguments.size() < 5) {
                    throw new CommandException(name(), "Missing the duration");
                }

                builder.duration(parseDuration(arguments.get(4)));
                causeOffset = 5;
                break;

            case "forever":
                causeOffset = 4;
                break;

            default:
                throw new CommandException(name(), "Invalid duration");
        }

        builder.cause(cause(arguments, causeOffset));
        performer.account().ifPresent(builder::banisher);
        builder.apply();

        performer.success("The IP address {} has been banned.", ipAddress.toNormalizedString());
    }

    /**
     * Render a ban ip rule
     */
    private String render(BanIpRule<GameAccount> rule) {
        return rule.ipAddress().toNormalizedString() + " " +
            rule.expiresAt().map(instant -> "until " + instant).orElse("forever") + " " +
            "(by " + rule.banisher().map(LivingAccount::pseudo).orElse("system") + ") - " +
            rule.cause() + " " +
            Link.Type.EXECUTE.create("${server} banip remove " + rule.ipAddress().toNormalizedString()).text("remove")
        ;
    }

    /**
     * Extract the ban cause
     */
    private String cause(List<String> arguments, int offset) throws CommandException {
        if (arguments.size() <= offset) {
            throw new CommandException(name(), "Missing cause");
        }

        return arguments.stream().skip(offset).collect(Collectors.joining(" "));
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
