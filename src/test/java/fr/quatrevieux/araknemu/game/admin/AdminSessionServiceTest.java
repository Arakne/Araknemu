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

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class AdminSessionServiceTest extends GameBaseCase {
    private AdminSessionService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new AdminSessionService(
            container.get(AdminUser.Factory.class)
        );
    }

    @Test
    void userWillCreateAnAdminUser() throws SQLException, ContainerException, AdminException {
        AdminUser user = service.user(gamePlayer());

        assertEquals(gamePlayer().id(), user.id());
    }

    @Test
    void userGetSameUser() throws SQLException, ContainerException, AdminException {
        assertSame(
            service.user(gamePlayer()),
            service.user(gamePlayer())
        );
    }

    @Test
    void userGetAndDisconnectWillRemovePlayer() throws SQLException, ContainerException, AdminException {
        AdminUser user = service.user(gamePlayer());

        gamePlayer().dispatch(new Disconnected());

        assertNotSame(user, service.user(gamePlayer()));
    }
}
