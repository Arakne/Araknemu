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

package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasicCellTest extends GameBaseCase {
    private MapTemplateRepository repository;
    private ExplorationMapService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        repository = container.get(MapTemplateRepository.class);
        service = container.get(ExplorationMapService.class);
    }

    @Test
    void map() {
        ExplorationMap map = service.load(10340);

        BasicCell cell = new BasicCell(185, repository.get(10340).cells()[185], map);

        assertSame(map, cell.map());
    }

    @Test
    void walkableCell() {
        BasicCell cell = new BasicCell(185, repository.get(10340).cells()[185], service.load(10340));

        assertEquals(185, cell.id());
        assertTrue(cell.walkable());
    }

    @Test
    void deactivatedCell() {
        BasicCell cell = new BasicCell(0, repository.get(10340).cells()[0], service.load(10340));

        assertEquals(0, cell.id());
        assertFalse(cell.walkable());
    }

    @Test
    void unwalkableCell() {
        BasicCell cell = new BasicCell(209, repository.get(10340).cells()[209], service.load(10340));

        assertEquals(209, cell.id());
        assertFalse(cell.walkable());
    }

    @Test
    void equals() {
        BasicCell cell = new BasicCell(185, repository.get(10340).cells()[185], service.load(10340));

        assertEquals(cell, cell);
        assertEquals(cell, new BasicCell(185, repository.get(10340).cells()[185], service.load(10340)));
        assertNotEquals(cell, new BasicCell(180, repository.get(10340).cells()[180], service.load(10340)));
        assertNotEquals(cell, new Object());
    }

    @Test
    void free() {
        assertTrue(new BasicCell(185, repository.get(10340).cells()[185], service.load(10340)).free());
        assertFalse(new BasicCell(209, repository.get(10340).cells()[209], service.load(10340)).free());
    }

    @Test
    void freeWithCreature() throws Exception {
        ExplorationPlayer player = makeOtherExplorationPlayer();
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        player.changeMap(map, 123);

        player.changeCell(185);
        assertFalse(new BasicCell(185, repository.get(10340).cells()[185], service.load(10340)).free());

        player.changeCell(186);
        assertTrue(new BasicCell(185, repository.get(10340).cells()[185], service.load(10340)).free());
    }

    @Test
    void apply() throws Exception {
        ExplorationMap map = explorationPlayer().map();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        explorationPlayer().changeCell(279);
        other.changeMap(map, 279);

        Collection<ExplorationCreature> creatures = new ArrayList<>();

        map.get(279).apply(new Operation<Boolean>() {
            @Override
            public Boolean onCreature(ExplorationCreature creature) {
                creatures.add(creature);
                return true;
            }
        });

        assertEquals(Arrays.asList(explorationPlayer(), other), creatures);

        creatures.clear();
        other.changeCell(275);

        map.get(279).apply(new Operation<Boolean>() {
            @Override
            public Boolean onCreature(ExplorationCreature creature) {
                creatures.add(creature);
                return true;
            }
        });

        assertEquals(Arrays.asList(explorationPlayer()), creatures);
    }

    @Test
    void applyWithReturnFalseShouldSkipOtherCreatures() throws Exception {
        ExplorationMap map = explorationPlayer().map();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        explorationPlayer().changeCell(279);
        other.changeMap(map, 279);

        Collection<ExplorationCreature> creatures = new ArrayList<>();

        map.get(279).apply(new Operation<Boolean>() {
            @Override
            public Boolean onCreature(ExplorationCreature creature) {
                creatures.add(creature);
                return false;
            }
        });

        assertEquals(Arrays.asList(explorationPlayer()), creatures);
    }

    @Test
    void applyShouldNotFailedIfCreatureLeaveMapDuringApplication() throws Exception {
        ExplorationMap map = explorationPlayer().map();
        ExplorationPlayer me = explorationPlayer();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        explorationPlayer().changeCell(279);
        other.changeMap(map, 279);

        map.get(279).apply(new Operation<Boolean>() {
            @Override
            public Boolean onExplorationPlayer(ExplorationPlayer player) {
                me.leave();
                other.leave();
                return true;
            }
        });
    }
}
