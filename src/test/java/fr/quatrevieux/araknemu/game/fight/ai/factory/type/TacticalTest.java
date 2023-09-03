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

package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TacticalTest extends AiBaseCase {
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        actionFactory = new Tactical(container.get(Simulator.class));
        dataSet.pushFunctionalSpells();
    }

    @Test
    void shouldBoostFirst() {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(342))
            .addEnemy(fb -> fb.cell(327))
        );

        assertCast(6, 342);
    }

    @Test
    void shouldAttackIfCantBoost() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(342))
            .addEnemy(fb -> fb.cell(327))
        );

        removeSpell(6);

        assertCast(3, 327);
    }

    @Test
    void shouldMoveToAttackIfCantAttackFromTheCurrentCell() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(342))
            .addEnemy(fb -> fb.cell(241))
        );

        removeSpell(6);

        generateAndPerformMove();
        assertEquals(327, fighter.cell().id());

        assertCast(3, 241);
    }

    @Test
    void shouldMoveNearEnemyIfCantAttackBecauseToFar() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(210))
            .addEnemy(fb -> fb.cell(52))
        );

        removeSpell(6);
        assertEquals(11, distance(getEnemy(0)));

        generateAndPerformMove();

        assertEquals(165, fighter.cell().id());
        assertEquals(8, distance(getEnemy(0)));
    }

    @Test
    void shouldMoveFarEnemyIfCantAttackButInRange() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(210))
            .addEnemy(fb -> fb.cell(198))
        );

        setAP(2);
        removeSpell(6);
        assertEquals(5, distance(getEnemy(0)));

        generateAndPerformMove();

        assertEquals(165, fighter.cell().id());
        assertEquals(8, distance(getEnemy(0)));
    }

    @Test
    void shouldMoveTeleportNearEnemyIfCantAttackAndMove() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(210).spell(142, 5))
            .addEnemy(fb -> fb.cell(52))
        );

        setMP(0);

        removeSpell(6);
        assertEquals(11, distance(getEnemy(0)));

        assertCast(142, 136);
        performGeneratedAction();

        assertEquals(136, fighter.cell().id());
        assertEquals(6, distance(getEnemy(0)));
    }

    @Test
    void shouldBoostAlliesIfCantMoveOrAttack() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(210).spell(27, 5))
            .addAlly(fb -> fb.cell(198))
            .addEnemy(fb -> fb.cell(52))
        );

        setMP(0);

        removeSpell(6);

        assertCast(27, 183);
        assertInCastEffectArea(198);
    }

    @Test
    void shouldHealIfLessThan50PercentLife() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(298).spell(121).spell(81).currentLife(50).maxLife(100))
            .addAlly(fb -> fb.cell(22))
            .addEnemy(fb -> fb.cell(256))
        );

        setMP(0);
        removeSpell(6);
        removeSpell(3);

        assertCast(121, 298);
    }

    @Test
    void shouldDebuffIfCantBoostOrAttack() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(298).spell(121).spell(81).currentLife(80).maxLife(100))
            .addAlly(fb -> fb.cell(22))
            .addEnemy(fb -> fb.cell(256))
        );

        setMP(0);
        removeSpell(6);
        removeSpell(3);

        assertCast(81, 256);
    }

    @Test
    void shouldInvokeIfCantBoostOrAttack() throws NoSuchFieldException, IllegalAccessException, SQLException {
        dataSet
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;

        configureFight(b -> b
            .addSelf(fb -> fb.cell(298).spell(121).spell(81).spell(35).currentLife(80).maxLife(100))
            .addAlly(fb -> fb.cell(22))
            .addEnemy(fb -> fb.cell(256))
        );

        setMP(0);
        removeSpell(6);
        removeSpell(3);

        assertCast(35, 284);
    }

    @Test
    void shouldHealIfCantDebuff() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(298).spell(121).currentLife(80).maxLife(100))
            .addAlly(fb -> fb.cell(22))
            .addEnemy(fb -> fb.cell(256))
        );

        setMP(0);
        removeSpell(6);
        removeSpell(3);

        assertCast(121, 298);
    }

    @Test
    void shouldDoNothingOtherwise() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(210).spell(27, 5))
            .addAlly(fb -> fb.cell(22))
            .addEnemy(fb -> fb.cell(52))
        );

        setMP(0);
        removeSpell(6);

        assertDotNotGenerateAction();
    }

    private int distance(Fighter other) {
        return fighter.cell().coordinate().distance(other.cell());
    }
}
