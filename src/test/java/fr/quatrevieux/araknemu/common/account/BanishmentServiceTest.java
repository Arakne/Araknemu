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

package fr.quatrevieux.araknemu.common.account;

import fr.quatrevieux.araknemu.common.account.banishment.BanEntry;
import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.common.account.banishment.event.AccountBanned;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.data.living.repository.account.BanishmentRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BanishmentServiceTest extends GameBaseCase {
    private BanishmentService<GameAccount> service;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dispatcher = new DefaultListenerAggregate();
        service = new BanishmentService<>(
            container.get(BanishmentRepository.class),
            dispatcher,
            container.get(AccountService.class)::getByIds
        );
        dataSet.use(Banishment.class);
    }

    @Test
    void isBanned() {
        login();
        GameAccount account = session.account();

        assertFalse(service.isBanned(account));
        dataSet.push(new Banishment(1, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "test", 3));
        assertTrue(service.isBanned(account));
    }

    @Test
    void isBannedShouldIgnoreGameMasterAccount() {
        GameAccount account = new GameAccount(
            new Account(1, "", "", "", EnumSet.allOf(Permission.class), "", ""),
            container.get(AccountService.class),
            2
        );

        dataSet.push(new Banishment(1, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "test", 3));
        assertFalse(service.isBanned(account));
    }

    @Test
    void banWithoutBanisher() {
        AtomicReference<AccountBanned> ref = new AtomicReference<>();
        dispatcher.add(AccountBanned.class, ref::set);

        GameAccount account = new GameAccount(
            new Account(1, "", "", ""),
            container.get(AccountService.class),
            2
        );

        BanEntry<GameAccount> entry = service.ban(account, Duration.ofHours(1), "my cause");

        assertSame(account, entry.account());
        assertTrue(entry.active());
        assertFalse(entry.banisher().isPresent());
        assertTrue(entry.start().getEpochSecond() - Instant.now().getEpochSecond() < 2);
        assertBetween(3599, 3601, entry.end().getEpochSecond() - Instant.now().getEpochSecond());
        assertEquals("my cause", entry.cause());

        Banishment entity = container.get(BanishmentRepository.class).forAccount(1).get(0);

        assertEquals(1, entity.accountId());
        assertEquals(-1, entity.banisherId());
        assertEquals(entry.start().getEpochSecond(), entity.startDate().getEpochSecond());
        assertEquals(entry.end().getEpochSecond(), entity.endDate().getEpochSecond());
        assertEquals("my cause", entity.cause());

        assertTrue(service.isBanned(account));

        assertSame(entry, ref.get().entry());
    }

    @Test
    void banWithBanisher() {
        AtomicReference<AccountBanned> ref = new AtomicReference<>();
        dispatcher.add(AccountBanned.class, ref::set);

        GameAccount account = new GameAccount(
            new Account(1, "", "", ""),
            container.get(AccountService.class),
            2
        );

        GameAccount banisher = new GameAccount(
            new Account(4, "", "", ""),
            container.get(AccountService.class),
            2
        );

        BanEntry<GameAccount> entry = service.ban(account, Duration.ofHours(1), "my cause", banisher);

        assertSame(account, entry.account());
        assertTrue(entry.active());
        assertSame(banisher, entry.banisher().get());
        assertTrue(entry.start().getEpochSecond() - Instant.now().getEpochSecond() < 2);
        assertBetween(3599, 3601, entry.end().getEpochSecond() - Instant.now().getEpochSecond());
        assertEquals("my cause", entry.cause());

        Banishment entity = container.get(BanishmentRepository.class).forAccount(1).get(0);

        assertEquals(1, entity.accountId());
        assertEquals(4, entity.banisherId());
        assertEquals(entry.start().getEpochSecond(), entity.startDate().getEpochSecond());
        assertEquals(entry.end().getEpochSecond(), entity.endDate().getEpochSecond());
        assertEquals("my cause", entity.cause());

        assertTrue(service.isBanned(account));

        assertSame(entry, ref.get().entry());
    }

    @Test
    void list() {
        GameAccount account = new GameAccount(
            new Account(1, "", "", ""),
            container.get(AccountService.class),
            2
        );

        Account a1 = dataSet.push(new Account(-1, "a1", "a1", "a1", Collections.emptySet(), "", ""));
        Account a2 = dataSet.push(new Account(-1, "a2", "a2", "a2", Collections.emptySet(), "", ""));

        assertCount(0, service.list(account));

        dataSet.push(new Banishment(account.id(), Instant.parse("2020-07-25T15:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "ban 1", -1));
        dataSet.push(new Banishment(account.id(), Instant.parse("2020-07-25T16:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "ban 2", a1.id()));
        dataSet.push(new Banishment(account.id(), Instant.parse("2020-07-25T17:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "ban 3", a1.id()));
        dataSet.push(new Banishment(account.id(), Instant.parse("2020-07-25T18:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "ban 4", a2.id()));

        List<BanEntry<GameAccount>> entries = service.list(account);
        assertCount(4, entries);

        assertEquals(account, entries.get(0).account());
        assertEquals("ban 4", entries.get(0).cause());
        assertEquals("a2", entries.get(0).banisher().get().pseudo());
        assertEquals(account, entries.get(1).account());
        assertEquals("ban 3", entries.get(1).cause());
        assertEquals("a1", entries.get(1).banisher().get().pseudo());
        assertEquals(account, entries.get(2).account());
        assertEquals("ban 2", entries.get(2).cause());
        assertEquals("a1", entries.get(2).banisher().get().pseudo());
        assertEquals(account, entries.get(3).account());
        assertEquals("ban 1", entries.get(3).cause());
        assertFalse(entries.get(3).banisher().isPresent());
    }

    @Test
    void unban() {
        GameAccount account = new GameAccount(
            new Account(1, "", "", ""),
            container.get(AccountService.class),
            2
        );

        dataSet.push(new Banishment(account.id(), Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "cause", -1));
        dataSet.push(new Banishment(account.id(), Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "cause", -1));
        dataSet.push(new Banishment(account.id(), Instant.now().minus(2, ChronoUnit.HOURS), Instant.now().minus(1, ChronoUnit.HOURS), "old", -1));

        assertTrue(service.isBanned(account));
        service.unban(account);
        assertFalse(service.isBanned(account));

        assertCount(1, service.list(account));
        assertEquals("old", service.list(account).get(0).cause());
    }
}
