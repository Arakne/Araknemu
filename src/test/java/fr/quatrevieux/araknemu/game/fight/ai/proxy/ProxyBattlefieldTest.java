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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.proxy;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProxyBattlefieldTest extends AiBaseCase {
    @Test
    void baseValues() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyBattlefield battlefield = new ProxyBattlefield(ai.map());

        assertEquals(battlefield.size(), ai.map().size());
        assertSame(battlefield.dimensions(), ai.map().dimensions());
        assertIterableEquals(battlefield, ai.map());
        assertSame(battlefield.get(123), ai.map().get(123));
    }

    @Test
    void modifyWithFreeCell() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyBattlefield battlefield = new ProxyBattlefield(ai.map());
        ProxyBattlefield modified = battlefield.modify(modifier -> {
            modifier.free(152);
        });

        assertNotSame(battlefield, modified);
        assertEquals(modified.size(), ai.map().size());
        assertSame(modified.dimensions(), ai.map().dimensions());

        assertNotSame(modified.get(123), battlefield.get(123));

        assertNull(modified.get(152).fighter());
        assertFalse(modified.get(152).hasFighter());
        assertTrue(modified.get(152).walkableIgnoreFighter());
        assertTrue(modified.get(152).walkable());
        assertFalse(modified.get(152).sightBlocking());

        assertSame(ai.fighters().collect(Collectors.toList()).get(2), modified.get(166).fighter());
        assertTrue(modified.get(166).walkableIgnoreFighter());
        assertFalse(modified.get(166).walkable());
        assertTrue(modified.get(166).sightBlocking());
    }

    @Test
    void modifyWithSetFighter() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyBattlefield battlefield = new ProxyBattlefield(ai.map());
        ProxyBattlefield modified = battlefield.modify(modifier -> {
            modifier.setFighter(123, new ProxyPassiveFighter(ai.fighter(), ai));
        });

        assertNotSame(battlefield, modified);
        assertEquals(modified.size(), ai.map().size());
        assertSame(modified.dimensions(), ai.map().dimensions());

        assertTrue(modified.get(123).hasFighter());
        assertInstanceOf(ProxyPassiveFighter.class, modified.get(123).fighter());
        assertTrue(modified.get(123).walkableIgnoreFighter());
        assertFalse(modified.get(123).walkable());
        assertTrue(modified.get(123).sightBlocking());
    }

    @Test
    void cellSightBlocking() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyBattlefield battlefield = new ProxyBattlefield(ai.map()).modify(modifier -> {});

        assertTrue(battlefield.get(152).sightBlocking());
        assertFalse(battlefield.get(153).sightBlocking());
        assertTrue(battlefield.get(154).sightBlocking());
        assertTrue(battlefield.get(167).sightBlocking());

        battlefield = battlefield.modify(modifier -> modifier.free(152).free(154));

        assertFalse(battlefield.get(152).sightBlocking());
        assertFalse(battlefield.get(153).sightBlocking());
        assertTrue(battlefield.get(154).sightBlocking());
        assertTrue(battlefield.get(167).sightBlocking());

        battlefield = battlefield.modify(modifier -> modifier.setFighter(153, Mockito.mock(FighterData.class)));

        assertFalse(battlefield.get(152).sightBlocking());
        assertTrue(battlefield.get(153).sightBlocking());
        assertTrue(battlefield.get(154).sightBlocking());
        assertTrue(battlefield.get(167).sightBlocking());
    }

    @Test
    void cellWalkable() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyBattlefield battlefield = new ProxyBattlefield(ai.map()).modify(modifier -> {});

        assertFalse(battlefield.get(152).walkable());
        assertTrue(battlefield.get(153).walkable());
        assertFalse(battlefield.get(154).walkable());
        assertFalse(battlefield.get(167).walkable());

        battlefield = battlefield.modify(modifier -> modifier.free(152).free(154));

        assertTrue(battlefield.get(152).walkable());
        assertTrue(battlefield.get(153).walkable());
        assertFalse(battlefield.get(154).walkable());
        assertFalse(battlefield.get(167).walkable());

        battlefield = battlefield.modify(modifier -> modifier.setFighter(153, Mockito.mock(FighterData.class)));

        assertTrue(battlefield.get(152).walkable());
        assertFalse(battlefield.get(153).walkable());
        assertFalse(battlefield.get(154).walkable());
        assertFalse(battlefield.get(167).walkable());
    }

    @Test
    void cellValues() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyBattlefield battlefield = new ProxyBattlefield(ai.map()).modify(modifier -> {});

        for (BattlefieldCell cell : battlefield) {
            assertSame(battlefield, cell.map());
            assertEquals(cell.walkableIgnoreFighter(), ai.map().get(cell.id()).walkableIgnoreFighter());
            assertEquals(cell.fighter(), ai.map().get(cell.id()).fighter());
            assertEquals(cell.sightBlocking(), ai.map().get(cell.id()).sightBlocking());
            assertEquals(cell.id(), ai.map().get(cell.id()).id());
            assertEquals(cell.walkable(), ai.map().get(cell.id()).walkable());
            assertEquals(cell.coordinate().x(), ai.map().get(cell.id()).coordinate().x());
            assertEquals(cell.coordinate().y(), ai.map().get(cell.id()).coordinate().y());
            assertSame(cell.coordinate(), cell.coordinate());
        }
    }
}
