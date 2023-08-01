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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProxyPassiveFighterTest extends AiBaseCase {
    @Test
    void baseValues() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyPassiveFighter fighter = new ProxyPassiveFighter(ai.enemy().get(), ai);

        assertSame(ai.enemy().get().characteristics(), fighter.characteristics());
        assertSame(ai.enemy().get().cell(), fighter.cell());
        assertSame(ai.enemy().get().life(), fighter.life());
        assertSame(ai.enemy().get().buffs(), fighter.buffs());
        assertSame(ai.enemy().get().states(), fighter.states());
        assertSame(ai.enemy().get().team(), fighter.team());
        assertSame(ai.enemy().get().sprite(), fighter.sprite());
        assertSame(ai.enemy().get().orientation(), fighter.orientation());
        assertNull(fighter.invoker());
        assertFalse(fighter.invoked());

        assertEquals(ai.enemy().get().id(), fighter.id());
        assertEquals(ai.enemy().get().dead(), fighter.dead());
        assertEquals(ai.enemy().get().hidden(), fighter.hidden());
    }

    @Test
    void invocation() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        DoubleFighter inner = new DoubleFighter(-5, player.fighter());
        fight.fighters().joinTurnList(inner, fight.map().get(168));
        ProxyPassiveFighter fighter = new ProxyPassiveFighter(inner, ai);

        assertSame(inner.characteristics(), fighter.characteristics());
        assertSame(inner.cell(), fighter.cell());
        assertSame(inner.life(), fighter.life());
        assertSame(inner.buffs(), fighter.buffs());
        assertSame(inner.states(), fighter.states());
        assertSame(inner.team(), fighter.team());
        assertSame(inner.sprite(), fighter.sprite());
        assertSame(inner.orientation(), fighter.orientation());
        assertSame(player.fighter(), fighter.invoker());
        assertTrue(fighter.invoked());

        assertEquals(inner.id(), fighter.id());
        assertEquals(inner.dead(), fighter.dead());
        assertEquals(inner.hidden(), fighter.hidden());
    }

    @Test
    void equalsAndHashCode() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyPassiveFighter fighter = new ProxyPassiveFighter(ai.enemy().get(), ai);

        assertEquals(fighter, fighter);
        assertEquals(fighter, ai.enemy().get());
        assertEquals(ai.enemy().get(), fighter);

        assertNotEquals(fighter, null);
        assertNotEquals(fighter, new Object());
        assertNotEquals(fighter, ai.fighter());

        assertEquals(fighter.hashCode(), ai.enemy().get().hashCode());
        assertNotEquals(fighter.hashCode(), ai.fighter().hashCode());
    }

    @Test
    void withOverriddenMap() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyAI proxyAi = new ProxyAI(ai).withPosition(123);

        ProxyPassiveFighter fighter = new ProxyPassiveFighter(ai.enemy().get(), proxyAi);

        assertSame(proxyAi.map().get(167), fighter.cell());
        assertNotSame(ai.map().get(167), fighter.cell());
    }
}
