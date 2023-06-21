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

package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredicatesTest extends AiBaseCase {
    @Test
    void hasEnemyInRangeSuccess() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        assertTrue(Predicates.<PlayableFighter>hasEnemyInRange().test(ai));
    }

    @Test
    void hasEnemyInRangeWithoutAttackSpells() throws NoSuchFieldException, IllegalAccessException {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        removeSpell(3);

        assertFalse(Predicates.<PlayableFighter>hasEnemyInRange().test(ai));
    }

    @Test
    void hasEnemyInRangeTooFar() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(256))
        );

        assertFalse(Predicates.<PlayableFighter>hasEnemyInRange().test(ai));
    }

    @Test
    void hasAllies() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(135))
            .addAlly(b -> b.cell(125))
        );

        assertTrue(Predicates.<PlayableFighter>hasAllies().test(ai));

        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(135))
            .addAlly(b -> b.cell(125))
            .addAlly(b -> b.cell(125))
        );

        assertTrue(Predicates.<PlayableFighter>hasAllies().test(ai));

        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(135))
        );

        assertFalse(Predicates.<PlayableFighter>hasAllies().test(ai));
    }

    @Test
    void hasLessThanPercentLife() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123).currentLife(50).maxLife(100))
            .addEnemy(b -> b.cell(256))
        );

        assertFalse(Predicates.<PlayableFighter>hasLessThanPercentLife(10).test(ai));
        assertTrue(Predicates.<PlayableFighter>hasLessThanPercentLife(50).test(ai));
        assertTrue(Predicates.<PlayableFighter>hasLessThanPercentLife(51).test(ai));
    }
}
