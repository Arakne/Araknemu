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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.NullContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerContextResolverTest extends GameBaseCase {
    private PlayerContextResolver resolver;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(Player.class);

        resolver = new PlayerContextResolver(
            container.get(PlayerService.class),
            container.get(AccountContextResolver.class),
            container.get(ItemService.class),
            container.get(GeolocationService.class),
            container.get(ExplorationMapService.class)
        );
    }

    @Test
    void resolveByGamePlayer() throws SQLException, ContainerException, ContextException {
        Context context = resolver.resolve(
            new NullContext(),
            gamePlayer()
        );

        assertInstanceOf(PlayerContext.class, context);
        assertInstanceOf(AccountContext.class, context.child("account"));
    }

    @Test
    void resolveByName() throws SQLException, ContainerException, ContextException {
        gamePlayer(true);

        Context context = resolver.resolve(new NullContext(), "Bob");

        assertInstanceOf(PlayerContext.class, context);
        assertInstanceOf(AccountContext.class, context.child("account"));
    }

    @Test
    void resolveInvalidArgument() {
        assertThrows(ContextException.class, () -> resolver.resolve(new NullContext(), new Object()));
    }

    @Test
    void resolvePlayerNotFound() {
        assertThrows(ContextException.class, () -> resolver.resolve(new NullContext(), "notFound"), "Cannot found the player notFound");
    }
}
