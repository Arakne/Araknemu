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
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.global.GlobalContext;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContext;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContextResolver;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest extends GameBaseCase {
    private AdminService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new AdminService(
            container.get(GlobalContext.class),
            Arrays.asList(
                container.get(PlayerContextResolver.class),
                container.get(AccountContextResolver.class)
            ),
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

    @Test
    void contextNotFound() {
        assertThrows(ContextException.class, () -> service.context("not_found", null));
    }

    @Test
    void contextSuccess() throws SQLException, ContextException {
        Context context = service.context("player", gamePlayer(true).name());

        assertInstanceOf(PlayerContext.class, context);
        assertEquals(gamePlayer(), ((PlayerContext) (context)).player());
    }
}
