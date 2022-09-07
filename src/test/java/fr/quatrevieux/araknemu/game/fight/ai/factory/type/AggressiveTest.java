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
import fr.quatrevieux.araknemu.game.fight.ai.action.builder.GeneratorBuilder;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggressiveTest extends AiBaseCase {
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        actionFactory = new Aggressive(container.get(Simulator.class));
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
    void shouldMoveNearEnemyIfCantAttack() throws NoSuchFieldException, IllegalAccessException {
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
    void shouldHealIfCantMoveOrAttackOrBoost() throws NoSuchFieldException, IllegalAccessException {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(210).spell(121).currentLife(50).maxLife(100))
            .addAlly(fb -> fb.cell(22))
            .addEnemy(fb -> fb.cell(52))
        );

        setMP(0);
        removeSpell(6);

        assertCast(121, 210);
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

    @Test
    void withoutDistanceSpellsShouldMoveNearEnemy() throws NoSuchFieldException, IllegalAccessException {
        SpellBook spells = player.properties().spells();
        Field field = spells.getClass().getDeclaredField("entries");
        field.setAccessible(true);

        ((Map) field.get(spells)).clear();

        configureFight(b -> b
            .addSelf(fb -> fb.cell(250).spell(145, 5))
            .addEnemy(fb -> fb.cell(292))
        );

        assertEquals(3, distance(getEnemy(0)));
        generateAndPerformMove();
        assertEquals(2, distance(getEnemy(0)));
    }

    @Test
    void withoutDistanceSpellsShouldAttack() throws NoSuchFieldException, IllegalAccessException {
        SpellBook spells = player.properties().spells();
        Field field = spells.getClass().getDeclaredField("entries");
        field.setAccessible(true);

        ((Map) field.get(spells)).clear();

        configureFight(b -> b
            .addSelf(fb -> fb.cell(277).spell(145, 5))
            .addEnemy(fb -> fb.cell(292))
        );

        assertCast(145, 277);
    }

    @Test
    void withoutDistanceSpellsButWithAreaShouldBeConsideredAsDistanceSpell() throws NoSuchFieldException, IllegalAccessException {
        SpellBook spells = player.properties().spells();
        Field field = spells.getClass().getDeclaredField("entries");
        field.setAccessible(true);

        ((Map) field.get(spells)).clear();

        configureFight(b -> b
            .addSelf(fb -> fb.cell(192).spell(181, 5))
            .addEnemy(fb -> fb.cell(221))
            .addEnemy(fb -> fb.cell(263))
        );

        assertCast(181, 192);
    }

    private int distance(Fighter other) {
        return fighter.cell().coordinate().distance(other.cell());
    }
}
