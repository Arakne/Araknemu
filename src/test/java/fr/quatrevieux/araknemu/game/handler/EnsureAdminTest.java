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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.handler.account.ListCharacters;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnsureAdminTest extends GameBaseCase {
    @Test
    void handleNotLogged() {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureAdmin handler = new EnsureAdmin<>(inner);

        assertThrows(CloseImmediately.class, () -> handler.handle(session, Mockito.mock(Packet.class)));
    }

    @Test
    void handleSuccess() throws Exception {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureAdmin handler = new EnsureAdmin<>(inner);
        session.attach(
            new GameAccount(
                new Account(1, "", "", "", EnumSet.allOf(Permission.class), "", ""),
                container.get(AccountService.class),
                1
            )
        );

        gamePlayer();

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);

        Mockito.verify(inner).handle(session, packet);
    }

    @Test
    void handleNotAdmin() throws Exception {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureAdmin handler = new EnsureAdmin<>(inner);
        session.attach(
            new GameAccount(
                new Account(1, "", "", "", EnumSet.noneOf(Permission.class), "", ""),
                container.get(AccountService.class),
                1
            )
        );

        assertThrows(CloseImmediately.class, () -> handler.handle(session, Mockito.mock(Packet.class)));
    }

    @Test
    void packet() throws ContainerException {
        assertEquals(
            AskCharacterList.class,
            new EnsureAdmin<>(new ListCharacters(
                container.get(CharactersService.class)
            )).packet()
        );
    }
}
