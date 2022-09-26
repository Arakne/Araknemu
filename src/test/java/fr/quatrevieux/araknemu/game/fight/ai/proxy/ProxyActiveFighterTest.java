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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProxyActiveFighterTest extends AiBaseCase {
    @Test
    void baseValues() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyActiveFighter fighter = new ProxyActiveFighter(ai.fighter());

        assertSame(ai.fighter().characteristics(), fighter.characteristics());
        assertSame(ai.fighter().spells(), fighter.spells());
        assertSame(ai.fighter().cell(), fighter.cell());
        assertSame(ai.fighter().life(), fighter.life());
        assertSame(ai.fighter().buffs(), fighter.buffs());
        assertSame(ai.fighter().states(), fighter.states());
        assertSame(ai.fighter().team(), fighter.team());
        assertSame(ai.fighter().sprite(), fighter.sprite());
        assertSame(ai.fighter().orientation(), fighter.orientation());
        assertSame(fight, fighter.fight());

        assertEquals(ai.fighter().id(), fighter.id());
        assertEquals(ai.fighter().dead(), fighter.dead());
        assertEquals(ai.fighter().hidden(), fighter.hidden());
    }

    @Test
    void equalsAndHashCode() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyActiveFighter fighter = new ProxyActiveFighter(ai.fighter());

        assertEquals(fighter, fighter);
        assertEquals(fighter, ai.fighter());
        assertEquals(ai.fighter(), fighter);

        assertNotEquals(fighter, null);
        assertNotEquals(fighter, new Object());
        assertNotEquals(fighter, ai.enemy().get());

        assertEquals(fighter.hashCode(), ai.fighter().hashCode());
        assertNotEquals(fighter.hashCode(), ai.enemy().get().hashCode());
    }

    @Test
    void withPosition() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        ProxyActiveFighter fighter = new ProxyActiveFighter(ai.fighter());
        ProxyActiveFighter withPosition = fighter.withPosition(ai.map().get(123));

        assertNotSame(fighter, withPosition);
        assertEquals(fighter, withPosition);

        assertEquals(152, fighter.cell().id());
        assertEquals(123, withPosition.cell().id());
        assertSame(ai.map().get(123), withPosition.cell());
    }

    @Test
    void attachments() {
        class Foo {}

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152))
            .addEnemy(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(166))
        );

        Foo foo = new Foo();
        Fighter.class.cast(ai.fighter()).attach(foo);

        ProxyActiveFighter fighter = new ProxyActiveFighter(ai.fighter());

        assertSame(foo, fighter.attachment(Foo.class));
        assertSame(foo, fighter.attachment((Object) Foo.class));
    }
}
