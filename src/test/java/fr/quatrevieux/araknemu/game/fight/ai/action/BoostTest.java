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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoostTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushFunctionalSpells();
    }

    @Test
    void success() {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(126, 5))
            .addEnemy(builder -> builder.cell(125))
        );

        assertCast(126, 122);
    }

    @Test
    void notEnoughAP() {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(126, 5))
            .addEnemy(builder -> builder.cell(125))
        );

        setAP(1);

        assertDotNotGenerateAction();
    }

    @Test
    void notAP() {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(126, 5))
            .addEnemy(builder -> builder.cell(125))
        );
        removeAllAP();

        assertDotNotGenerateAction();
    }

    @Test
    void withAreaSpell() {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(584, 5))
            .addAlly(builder -> builder.cell(78))
            .addAlly(builder -> builder.cell(92))
            .addAlly(builder -> builder.cell(77))
            .addEnemy(builder -> builder.cell(125))
        );

        assertCast(584, 64);

        assertInCastEffectArea(122, 78, 92, 77);
        assertNotInCastEffectArea(125);
    }

    @Test
    void selfBoostShouldIgnoreSpellWithoutDelay() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.self(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(148)) // Amplification
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertDotNotGenerateAction();
    }

    @Test
    void allyBoostShouldAllowSpellWithoutDelay() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(148, 5)) // Amplification
            .addAlly(builder -> builder.cell(136))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertCast(148, 136);
    }

    @Test
    void allyBoostShouldShouldIgnoreBoostSpellIfGeneratesTooManyDamage() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        // Ally is on adjacent cell
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145).charac(Characteristic.AGILITY, 300)) // épée divine
            .addAlly(builder -> builder.cell(136).currentLife(200))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertDotNotGenerateAction();

        // Ally is away
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145)) // épée divine
            .addAlly(builder -> builder.cell(95))
            .addEnemy(builder -> builder.cell(125))
        );

        assertCast(145, 122);
    }

    @Test
    void allyBoostShouldAllowsDamageIfLow() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        // Ally is on adjacent cell
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145).charac(Characteristic.AGILITY, -50)) // épée divine
            .addAlly(builder -> builder.cell(136))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertCast(145, 122);
        assertInCastEffectArea(136);
    }

    @Test
    void allyBoostShouldFavorBoostWithoutDamage() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        // Ally is on adjacent cell
        // divine sword (145) and amplification (148)
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145, 5).spell(148).charac(Characteristic.AGILITY, -50))
            .addAlly(builder -> builder.cell(136))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertCast(148, 136);
    }

    @Test
    void allyBoostShouldShouldIgnoreBoostSpellIfKillAlly() throws NoSuchFieldException, IllegalAccessException {
        action = Boost.allies(container.get(Simulator.class));

        // Ally is on adjacent cell
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spell(145)) // épée divine
            .addAlly(builder -> builder.cell(136).currentLife(1))
            .addEnemy(builder -> builder.cell(125))
        );

        removeSpell(6);

        assertDotNotGenerateAction();
    }
}
