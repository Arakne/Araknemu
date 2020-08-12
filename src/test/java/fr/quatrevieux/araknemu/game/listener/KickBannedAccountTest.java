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

package fr.quatrevieux.araknemu.game.listener;

import fr.quatrevieux.araknemu.common.account.banishment.BanEntry;
import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.common.account.banishment.event.AccountBanned;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;

class KickBannedAccountTest extends GameBaseCase {
    private KickBannedAccount listener;
    private AccountService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new KickBannedAccount();
        service = container.get(AccountService.class);
    }

    @Test
    void listenerShouldKickAccountOnBan() {
        dataSet.use(Banishment.class);
        Account account1 = dataSet.push(new Account(-1, "name", "pass", "pseudo", Collections.emptySet(), "", ""));
        GameAccount account = new GameAccount(account1, service, 1);
        account.attach(session);

        GameAccount banisher = new GameAccount(new Account(-1, "banisher", "pass", "banisher", Collections.emptySet(), "", ""), service, 1);

        BanEntry<GameAccount> entry = ((BanishmentService<GameAccount>) container.get(BanishmentService.class)).ban(account, Duration.ofHours(1), "cause", banisher);

        listener.on(new AccountBanned<>(entry));

        requestStack.assertLast(ServerMessage.kick(banisher.pseudo(), "cause"));
        assertFalse(session.isAlive());
    }

    @Test
    void listenerShouldKickAccountOnBanWithoutBanisher() {
        dataSet.use(Banishment.class);
        Account account1 = dataSet.push(new Account(-1, "name", "pass", "pseudo", Collections.emptySet(), "", ""));
        GameAccount account = new GameAccount(account1, service, 1);
        account.attach(session);

        BanEntry<GameAccount> entry = ((BanishmentService<GameAccount>) container.get(BanishmentService.class)).ban(account, Duration.ofHours(1), "cause");

        listener.on(new AccountBanned<>(entry));

        requestStack.assertLast(ServerMessage.kick("system", "cause"));
        assertFalse(session.isAlive());
    }
}
