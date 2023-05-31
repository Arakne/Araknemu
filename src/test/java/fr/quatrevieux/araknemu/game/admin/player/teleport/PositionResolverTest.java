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

package fr.quatrevieux.araknemu.game.admin.player.teleport;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.map.TeleportationTarget;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PositionResolverTest extends GameBaseCase {
    private PositionResolver resolver;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushSubAreas()
            .pushAreas()
            .pushMaps()
        ;

        resolver = new PositionResolver(gamePlayer(), container.get(GeolocationService.class));
    }

    @Test
    void resolve() throws SQLException {
        explorationPlayer();
        TeleportationTarget target = new TeleportationTarget(explorationPlayer().map(), 123);

        target = resolver.resolve("3;6", target);

        assertEquals(123, target.cell());
        assertEquals(container.get(ExplorationMapService.class).load(10340), target.map());
    }

    @Test
    void resolveWithComma() throws SQLException {
        explorationPlayer();
        TeleportationTarget target = new TeleportationTarget(explorationPlayer().map(), 123);

        target = resolver.resolve("3,6", target);

        assertEquals(123, target.cell());
        assertEquals(container.get(ExplorationMapService.class).load(10340), target.map());
    }

    @Test
    void resolveNotExploring() throws SQLException {
        TeleportationTarget target = new TeleportationTarget(explorationPlayer().map(), 123);

        target = resolver.resolve("3,6", target);

        assertEquals(123, target.cell());
        assertEquals(container.get(ExplorationMapService.class).load(10340), target.map());
    }

    @Test
    void resolveNotFound() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve("0;0", new TeleportationTarget(null, 0)));
        explorationPlayer();
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve("0;0", new TeleportationTarget(null, 0)));
    }
}
