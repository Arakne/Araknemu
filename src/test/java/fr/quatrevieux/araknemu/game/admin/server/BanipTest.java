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

import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.realm.out.LoginError;
import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BanipTest extends CommandTestCase {
    private BanIpService<GameAccount> service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Banip(service = container.get(BanIpService.class));
        dataSet.use(Account.class);
    }

    @Test
    void addForeverSuccess() throws SQLException, AdminException {
        execute("banip", "add", "12.36.54.98", "forever", "my", "ban", "cause");

        assertOutput("The IP address 12.36.54.98 has been banned.");
        assertTrue(service.isIpBanned(new IPAddressString("12.36.54.98")));
        assertFalse(service.matching(new IPAddressString("12.36.54.98")).get().expiresAt().isPresent());
        assertEquals(performer.account(), service.matching(new IPAddressString("12.36.54.98")).get().banisher());
        assertEquals("my ban cause", service.matching(new IPAddressString("12.36.54.98")).get().cause());
    }

    @Test
    void addWithDurationSuccess() throws SQLException, AdminException {
        execute("banip", "add", "12.36.54.98", "for", "2h", "my", "ban", "cause");

        assertOutput("The IP address 12.36.54.98 has been banned.");
        assertTrue(service.isIpBanned(new IPAddressString("12.36.54.98")));
        assertBetween(7199, 7201, service.matching(new IPAddressString("12.36.54.98")).get().expiresAt().get().getEpochSecond() - Instant.now().getEpochSecond());
        assertEquals(performer.account(), service.matching(new IPAddressString("12.36.54.98")).get().banisher());
        assertEquals("my ban cause", service.matching(new IPAddressString("12.36.54.98")).get().cause());
    }

    @Test
    void addWithMaskSuccess() throws SQLException, AdminException {
        execute("banip", "add", "12.36.0.0/16", "forever", "my", "ban", "cause");

        assertOutput("The IP address 12.36.0.0/16 has been banned.");
        assertTrue(service.isIpBanned(new IPAddressString("12.36.54.98")));
        assertEquals(performer.account(), service.matching(new IPAddressString("12.36.54.98")).get().banisher());
        assertEquals("my ban cause", service.matching(new IPAddressString("12.36.54.98")).get().cause());
    }

    @Test
    void addFunctionalShouldKickSession() throws SQLException, AdminException {
        GameSession session = server.createSession("12.36.54.98");

        execute("banip", "add", "12.36.0.0/16", "forever", "my", "ban", "cause");

        assertEquals(new LoginError(LoginError.BANNED).toString(), ((DummyChannel) session.channel()).getMessages().peek().toString());
        assertFalse(session.isAlive());
    }

    @Test
    void addBadParameters() {
        assertThrowsWithMessage(CommandException.class, "Argument \"IP_ADDRESS\" is required", () -> execute("banip", "add"));
        assertThrowsWithMessage(CommandException.class, "Invalid IP address given", () -> execute("banip", "add", "invalid"));
        assertThrowsWithMessage(CommandException.class, "Argument \"DURATION\" is required", () -> execute("banip", "add", "14.25.36.21"));
        assertThrowsWithMessage(CommandException.class, "\"invalid\" is not a valid value for \"DURATION\"", () -> execute("banip", "add", "14.25.36.21", "invalid"));
        assertThrowsWithMessage(CommandException.class, "Argument \"DURATION\" is required", () -> execute("banip", "add", "14.25.36.21", "for"));
        assertThrowsWithMessage(CommandException.class, "Option \"DURATION\" takes an operand", () -> execute("banip", "add", "14.25.36.21", "for", "invalid"));
        assertThrowsWithMessage(CommandException.class, "Argument \"MESSAGE\" is required", () -> execute("banip", "add", "14.25.36.21", "forever"));
        assertThrowsWithMessage(CommandException.class, "Argument \"MESSAGE\" is required", () -> execute("banip", "add", "14.25.36.21", "for", "1h"));
        assertThrowsWithMessage(CommandException.class, "Cannot ban your own IP address", () -> execute("banip", "add", "127.0.0.1", "forever", "cause"));
        assertThrowsWithMessage(CommandException.class, "Cannot ban your own IP address", () -> execute("banip", "add", "127.0.0.0/24", "forever", "cause"));
    }

    @Test
    void remove() throws SQLException, AdminException {
        service.newRule(new IPAddressString("12.36.54.98")).apply();

        execute("banip", "remove", "12.36.54.98");

        assertOutput("The IP address 12.36.54.98 has been unbanned.");
        assertFalse(service.isIpBanned(new IPAddressString("12.36.54.98")));
    }

    @Test
    void removeBadParameters() {
        assertThrows(CommandException.class, () -> execute("banip", "remove"));
        assertThrows(CommandException.class, () -> execute("banip", "remove", "invalid"));
    }

    @Test
    void listEmpty() throws SQLException, AdminException {
        execute("banip", "list");

        assertOutput("The ban ip table is empty");
    }

    @Test
    void list() throws SQLException, AdminException {
        Account banisher = dataSet.push(new Account(-1, "banisher", "", "banisher"));

        service.newRule(new IPAddressString("12.36.54.98")).cause("cause 1").apply();
        service.newRule(new IPAddressString("12.36.54.99")).cause("cause 2").banisher(container.get(AccountService.class).load(banisher)).apply();
        service.newRule(new IPAddressString("12.36.54.100")).cause("cause 3").duration(Duration.ofHours(1)).apply();

        execute("banip", "list");

        assertOutput(
            "List of ban ip rules :",
            "12.36.54.98 forever (by system) - cause 1 <u><a href='asfunction:onHref,ExecCmd,*banip remove 12.36.54.98,true'>remove</a></u>",
            "12.36.54.99 forever (by bob) - cause 2 <u><a href='asfunction:onHref,ExecCmd,*banip remove 12.36.54.99,true'>remove</a></u>",
            "12.36.54.100 until " + service.matching(new IPAddressString("12.36.54.100")).get().expiresAt().get() + " (by system) - cause 3 <u><a href='asfunction:onHref,ExecCmd,*banip remove 12.36.54.100,true'>remove</a></u>"
        );
    }

    @Test
    void checkNotMatching() throws SQLException, AdminException {
        execute("banip", "check", "14.25.66.78");

        assertOutput("The IP address 14.25.66.78 is not banned. <u><a href='asfunction:onHref,ExecCmd,*banip add 14.25.66.78 for,false'>add</a></u>");
    }

    @Test
    void checkMatching() throws SQLException, AdminException {
        service.newRule(new IPAddressString("14.25.66.0/24")).apply();

        execute("banip", "check", "14.25.66.78");

        assertOutput(
            "The IP address 14.25.66.78 is banned.",
            "Rule : 14.25.66.0/24 forever (by system) -  <u><a href='asfunction:onHref,ExecCmd,*banip remove 14.25.66.0/24,true'>remove</a></u>"
        );
    }

    @Test
    void checkInvalidArguments() {
        assertThrows(CommandException.class, () -> execute("banip", "check"));
        assertThrows(CommandException.class, () -> execute("banip", "check", "invalid"));
    }

    @Test
    void invalidOperation() {
        assertThrows(CommandException.class, () -> execute("banip"));
        assertThrows(CommandException.class, () -> execute("banip", "invalid"));
    }

    @Test
    void help() {
        assertHelp(
            "banip - Handle banned IP addresses",
            "========================================",
            "SYNOPSIS",
                "\tbanip [add|remove|list|check] ARGUMENTS",
            "OPTIONS",
                "\tadd IP_ADDRESS [for DURATION|forever] CAUSE : Add a new banned IP address. The IP address can be an IPv4 or IPv6 subnetwork mask.",
                "\tremove IP_ADDRESS : Remove a banned IP address.",
                "\tlist : Dump list of banned ip rules.",
                "\tcheck IP_ADDRESS : Check if the IP address is banned, and the ban rule.",
            "EXAMPLES",
                "\t*banip add 11.54.47.21 forever my ban message - Ban the IP address 11.54.47.21 forever",
                "\t*banip add 11.54.47.21 for 2h my ban message - Ban the IP address 11.54.47.21 for 2 hours",
                "\t*banip add 11.54.0.0/16 for 2h my ban message - Ban with a subnetwork mask",
                "\t*banip remove 11.54.52.32 - Remove the banned IP address 11.54.52.32",
                "\t*banip list - List all banned IP addresses",
                "\t*banip check 11.54.52.32 - Check if 11.54.52.32 is banned",
            "PERMISSIONS",
                "\t[ACCESS, SUPER_ADMIN]"
        );
    }
}
