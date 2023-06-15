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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProxyAITest extends AiBaseCase {
    @Test
    void baseProxy() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).charac(Characteristic.STRENGTH, 100))
            .addEnemy(builder -> builder.cell(167).charac(Characteristic.STRENGTH, 50))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyAI proxy = new ProxyAI(ai);

        assertNotSame(proxy.fighter(), ai.fighter());
        assertEquals(proxy.fighter(), ai.fighter());
        assertInstanceOf(ProxyActiveFighter.class, proxy.fighter());

        assertInstanceOf(ProxyBattlefield.class, proxy.map());
        assertInstanceOf(ProxyTurn.class, proxy.turn());

        assertInstanceOf(ProxyPassiveFighter.class, proxy.enemy().get());
        assertEquals(ai.enemy().get(), proxy.enemy().get());

        List<FighterData> fighters = proxy.fighters().collect(Collectors.toList());

        assertSame(proxy.fighter(), fighters.get(0));
        assertInstanceOf(ProxyPassiveFighter.class, fighters.get(1));
        assertInstanceOf(ProxyPassiveFighter.class, fighters.get(2));
        assertSame(proxy.enemy().get(), fighters.get(2));

        assertSame(proxy.fighter().cell(), ai.fighter().cell());
        assertSame(ai.map().get(fighters.get(1).cell().id()), fighters.get(1).cell());
        assertSame(ai.map().get(fighters.get(2).cell().id()), fighters.get(2).cell());

        assertEquals(6, proxy.turn().points().actionPoints());
        assertEquals(3, proxy.turn().points().movementPoints());

        setAP(3);
        setMP(2);

        assertEquals(3, proxy.turn().points().actionPoints());
        assertEquals(2, proxy.turn().points().movementPoints());
    }

    @Test
    void withPosition() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyAI proxy = new ProxyAI(ai);
        assertNotSame(proxy, proxy.withPosition(123));

        proxy = proxy.withPosition(123);

        assertEquals(proxy.fighter(), ai.fighter());
        assertInstanceOf(ProxyActiveFighter.class, proxy.fighter());

        assertInstanceOf(ProxyBattlefield.class, proxy.map());
        assertInstanceOf(ProxyTurn.class, proxy.turn());

        List<FighterData> fighters = proxy.fighters().collect(Collectors.toList());

        assertSame(proxy.fighter(), fighters.get(0));
        assertInstanceOf(ProxyPassiveFighter.class, fighters.get(1));
        assertInstanceOf(ProxyPassiveFighter.class, fighters.get(2));
        assertSame(proxy.enemy().get(), fighters.get(2));

        assertNotSame(proxy.fighter().cell(), ai.fighter().cell());
        assertNotSame(ai.map().get(fighters.get(1).cell().id()), fighters.get(1).cell());
        assertNotSame(ai.map().get(fighters.get(2).cell().id()), fighters.get(2).cell());

        assertEquals(123, proxy.fighter().cell().id());
        assertSame(proxy.map().get(123), proxy.fighter().cell());
        assertSame(proxy.fighter(), proxy.fighter().cell().fighter());
        assertEquals(166, fighters.get(1).cell().id());
        assertSame(proxy.map().get(166), fighters.get(1).cell());
        assertSame(fighters.get(1), fighters.get(1).cell().fighter());
        assertEquals(167, fighters.get(2).cell().id());
        assertSame(proxy.map().get(167), fighters.get(2).cell());
        assertSame(fighters.get(2), fighters.get(2).cell().fighter());

        assertFalse(proxy.map().get(152).hasFighter());
        assertTrue(ai.map().get(152).hasFighter());

        // Calling twice should works
        proxy = proxy.withPosition(125);

        fighters = proxy.fighters().collect(Collectors.toList());

        assertEquals(125, proxy.fighter().cell().id());
        assertSame(proxy.map().get(125), proxy.fighter().cell());
        assertSame(proxy.fighter(), proxy.fighter().cell().fighter());
        assertEquals(166, fighters.get(1).cell().id());
        assertSame(proxy.map().get(166), fighters.get(1).cell());
        assertSame(fighters.get(1), fighters.get(1).cell().fighter());
        assertEquals(167, fighters.get(2).cell().id());
        assertSame(proxy.map().get(167), fighters.get(2).cell());
        assertSame(fighters.get(2), fighters.get(2).cell().fighter());

        assertFalse(proxy.map().get(123).hasFighter());
        assertFalse(proxy.map().get(152).hasFighter());
    }

    @Test
    void startIsDisabled() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyAI proxy = new ProxyAI(ai);

        assertThrows(UnsupportedOperationException.class, () -> proxy.start(Mockito.mock(Turn.class)));
    }
}
