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

package fr.quatrevieux.araknemu.common.account.banishment;

import fr.quatrevieux.araknemu.common.account.banishment.event.IpBanned;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.BanIp;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.BanIpRepository;
import fr.quatrevieux.araknemu.data.living.transformer.InstantTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BanIpServiceTest extends GameBaseCase {
    private BanIpService<GameAccount> service;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new BanIpService<>(
            container.get(BanIpRepository.class),
            dispatcher = new DefaultListenerAggregate(),
            container.get(AccountService.class)::getByIds
        );

        dataSet.use(BanIp.class).use(Account.class);
    }

    @Test
    void newRule() {
        AtomicReference<IpBanned<GameAccount>> ref = new AtomicReference<>();
        dispatcher.add(IpBanned.class, ref::set);

        service.newRule(new IPAddressString("145.32.213.5")).apply();
        assertEquals("", ref.get().rule().cause());
        assertFalse(ref.get().rule().banisher().isPresent());
        assertFalse(ref.get().rule().expiresAt().isPresent());
        assertEquals(new IPAddressString("145.32.213.5"), ref.get().rule().ipAddress());
        assertEquals(new IPAddressString("145.32.213.5"), dataSet.refresh(new BanIp(1, null, null, null, null, -1)).ipAddress());
        assertEquals(-1, dataSet.refresh(new BanIp(1, null, null, null, null, -1)).banisherId());

        service.newRule(new IPAddressString("145.32.213.6")).duration(Duration.ofHours(1)).apply();
        assertEquals("", ref.get().rule().cause());
        assertFalse(ref.get().rule().banisher().isPresent());
        assertBetween(3599, 3601, ref.get().rule().expiresAt().get().getEpochSecond() - Instant.now().getEpochSecond());
        assertEquals(new IPAddressString("145.32.213.6"), ref.get().rule().ipAddress());
        assertEquals(new IPAddressString("145.32.213.6"), dataSet.refresh(new BanIp(2, null, null, null, null, -1)).ipAddress());
        assertEquals(-1, dataSet.refresh(new BanIp(2, null, null, null, null, -1)).banisherId());

        service.newRule(new IPAddressString("145.32.213.7")).cause("my ban cause").apply();
        assertEquals("my ban cause", ref.get().rule().cause());
        assertFalse(ref.get().rule().banisher().isPresent());
        assertFalse(ref.get().rule().expiresAt().isPresent());
        assertEquals(new IPAddressString("145.32.213.7"), ref.get().rule().ipAddress());
        assertEquals(new IPAddressString("145.32.213.7"), dataSet.refresh(new BanIp(3, null, null, null, null, -1)).ipAddress());
        assertEquals(-1, dataSet.refresh(new BanIp(3, null, null, null, null, -1)).banisherId());

        login();
        service.newRule(new IPAddressString("145.32.213.8")).banisher(session.account()).apply();
        assertEquals("", ref.get().rule().cause());
        assertSame(session.account(), ref.get().rule().banisher().get());
        assertFalse(ref.get().rule().expiresAt().isPresent());
        assertEquals(new IPAddressString("145.32.213.8"), ref.get().rule().ipAddress());
        assertEquals(new IPAddressString("145.32.213.8"), dataSet.refresh(new BanIp(4, null, null, null, null, -1)).ipAddress());
        assertEquals(session.account().id(), dataSet.refresh(new BanIp(4, null, null, null, null, -1)).banisherId());

        service.newRule(new IPAddressString("145.32.213.9"))
            .banisher(session.account())
            .cause("ban message")
            .duration(Duration.ofHours(2))
            .apply()
        ;

        assertEquals("ban message", ref.get().rule().cause());
        assertSame(session.account(), ref.get().rule().banisher().get());
        assertBetween(7199, 7201, ref.get().rule().expiresAt().get().getEpochSecond() - Instant.now().getEpochSecond());
        assertEquals(new IPAddressString("145.32.213.9"), ref.get().rule().ipAddress());
        assertEquals(new IPAddressString("145.32.213.9"), dataSet.refresh(new BanIp(5, null, null, null, null, -1)).ipAddress());
        assertEquals(session.account().id(), dataSet.refresh(new BanIp(5, null, null, null, null, -1)).banisherId());
    }

    @Test
    void isIpBanned() {
        IPAddressString toCheck = new IPAddressString("145.32.213.5");

        assertFalse(service.isIpBanned(toCheck));

        service.newRule(new IPAddressString("32.54.85.23")).apply();
        assertFalse(service.isIpBanned(toCheck));

        service.newRule(new IPAddressString("145.32.213.5")).duration(Duration.ofHours(-1)).apply();
        assertFalse(service.isIpBanned(toCheck));

        service.newRule(new IPAddressString("145.32.213.5")).apply();
        assertTrue(service.isIpBanned(toCheck));
        assertFalse(service.isIpBanned(new IPAddressString("145.32.213.6")));

        service.newRule(new IPAddressString("145.32.213.0/24")).apply();
        assertTrue(service.isIpBanned(new IPAddressString("145.32.213.6")));
    }

    @Test
    void matching() {
        Account account = dataSet.push(new Account(-1, "banisher", "", "banisher"));

        assertFalse(service.matching(new IPAddressString("145.32.213.5")).isPresent());

        service.newRule(new IPAddressString("145.32.213.5")).banisher(container.get(AccountService.class).load(account)).apply();
        assertEquals("banisher", service.matching(new IPAddressString("145.32.213.5")).get().banisher().get().pseudo());
    }

    @Test
    void rules() {
        Account account = dataSet.push(new Account(-1, "banisher", "", "banisher"));

        assertTrue(service.rules().isEmpty());

        service.newRule(new IPAddressString("145.32.213.5")).banisher(container.get(AccountService.class).load(account)).apply();
        service.newRule(new IPAddressString("145.32.213.6")).duration(Duration.ofHours(-1)).apply();
        service.newRule(new IPAddressString("145.32.213.7")).apply();

        List<BanIpRule<GameAccount>> rules = new ArrayList<>(service.rules());

        assertCount(2, rules);
        assertEquals(new IPAddressString("145.32.213.5"), rules.get(0).ipAddress());
        assertEquals("banisher", rules.get(0).banisher().get().pseudo());
        assertEquals(new IPAddressString("145.32.213.7"), rules.get(1).ipAddress());
        assertFalse(rules.get(1).banisher().isPresent());
    }

    @Test
    void refreshWithoutNewRuleShouldDoNothing() {
        service.newRule(new IPAddressString("145.32.213.5")).apply();
        service.newRule(new IPAddressString("145.32.213.6")).apply();
        service.load();

        AtomicReference<IpBanned<GameAccount>> ref = new AtomicReference<>();
        dispatcher.add(IpBanned.class, ref::set);

        service.refresh();

        assertNull(ref.get());
        assertCount(2, service.rules());
    }

    @Test
    void refreshWithNewRuleShouldLoadTheRuleAndDispatchIpBanned() {
        service.newRule(new IPAddressString("145.32.213.5")).apply();
        service.load();

        AtomicReference<IpBanned<GameAccount>> ref = new AtomicReference<>();
        dispatcher.add(IpBanned.class, ref::set);

        dataSet.push(new BanIp(new IPAddressString("145.32.213.6"), Instant.now().plus(1, ChronoUnit.SECONDS), null, "cause", -1)).id();
        service.refresh();

        assertEquals(new IPAddressString("145.32.213.6"), ref.get().rule().ipAddress());
        assertCount(2, service.rules());
        assertTrue(service.isIpBanned(new IPAddressString("145.32.213.6")));
    }

    @Test
    void refreshWithDisableRuleShouldRemove() throws SQLException {
        service.newRule(new IPAddressString("145.32.213.5")).apply();
        service.newRule(new IPAddressString("145.32.213.6")).apply();
        service.load();

        AtomicReference<IpBanned<GameAccount>> ref = new AtomicReference<>();
        dispatcher.add(IpBanned.class, ref::set);

        new ConnectionPoolExecutor(app.database().get("game")).prepare(
            "UPDATE BANIP SET EXPIRES_AT = ?, UPDATED_AT = ?  WHERE IP_ADDRESS = '145.32.213.5'",
            stmt -> {
                stmt.setString(1, new InstantTransformer().serialize(Instant.now().minus(10, ChronoUnit.SECONDS)));
                stmt.setString(2, new InstantTransformer().serialize(Instant.now().plus(10, ChronoUnit.SECONDS)));

                return stmt.execute();
            }
        );
        service.refresh();

        assertNull(ref.get());
        assertCount(1, service.rules());
        assertFalse(service.isIpBanned(new IPAddressString("145.32.213.5")));
        assertTrue(service.isIpBanned(new IPAddressString("145.32.213.6")));
    }

    @Test
    void load() {
        dataSet.push(new BanIp(new IPAddressString("145.32.213.5"), Instant.now(), null, "cause", -1));
        dataSet.push(new BanIp(new IPAddressString("145.32.213.6"), Instant.now(), Instant.now().minus(1, ChronoUnit.HOURS), "cause", -1));
        dataSet.push(new BanIp(new IPAddressString("145.32.213.7"), Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS), "cause", -1));

        assertCount(0, service.rules());

        service.load();
        assertCount(2, service.rules());
        assertTrue(service.isIpBanned(new IPAddressString("145.32.213.5")));
        assertFalse(service.isIpBanned(new IPAddressString("145.32.213.6")));
        assertTrue(service.isIpBanned(new IPAddressString("145.32.213.7")));
    }

    @Test
    void disable() {
        dataSet.push(new BanIp(new IPAddressString("145.32.213.5"), Instant.now(), null, "cause", -1));
        dataSet.push(new BanIp(new IPAddressString("145.32.213.6"), Instant.now(), null, "cause", -1));
        service.load();

        assertCount(2, service.rules());
        service.disable(new IPAddressString("145.32.213.5"));
        assertCount(1, service.rules());
        assertFalse(service.isIpBanned(new IPAddressString("145.32.213.5")));
    }
}
