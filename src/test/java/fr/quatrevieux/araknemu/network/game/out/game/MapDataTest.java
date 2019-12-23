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

package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapDataTest extends GameBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
    }

    @Test
    void generate() throws ContainerException {
        assertEquals(
            "GDM|10300|0802221747|662838776047515721434a62545f5478543541592532356f25324257716f6e28657d2a5d4471206273545f5e4a614a442c2c73432c35515a553b386f6525324274262f3a747b6b675440557f5260754d6a52343d433b52755e6e6c3b437d417e22514524216771304a6e7e553c32794646287f3e57544c48526a284e2158376b4a414039752c2a2f303d6a5e746f2e323031385e6f483e785929262435777c3141463f363a43784e73345f5e406d2e235d423248427767582c5e4a6a4d234e3f61506d32595e7a2c4a2532355a7f5b5c474e6f732532426062775824253235",
            new MapData(
                container.get(ExplorationMapService.class).load(10300)
            ).toString()
        );
    }
}