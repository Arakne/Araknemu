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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeleportNearEnemyTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new TeleportNearEnemy<>();
        dataSet.pushFunctionalSpells();
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(142, 5))
            .addEnemy(builder -> builder.cell(125))
        );

        assertCast(142, 110);
    }

    @Test
    void alreadyNearEnemy() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(110).spell(142, 5))
            .addEnemy(builder -> builder.cell(125))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void notEnoughAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(142, 5))
            .addEnemy(builder -> builder.cell(125))
        );

        setAP(1);

        assertDotNotGenerateAction();
    }

    @Test
    void noAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(142, 5))
            .addEnemy(builder -> builder.cell(125))
        );

        removeAllAP();

        assertDotNotGenerateAction();
    }

    @Test
    void noTeleportSpells() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.cell(125))
        );

        assertDotNotGenerateAction();
    }
}
