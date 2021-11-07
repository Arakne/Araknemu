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
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttackTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new Attack(container.get(Simulator.class));
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        assertCast(3, 125);
    }

    @Test
    void shouldKillEnemyWhenLowLife() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
            .addEnemy(builder -> builder.cell(135).currentLife(5))
        );

        assertCast(3, 135);
    }

    @Test
    void notEnoughAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        turn.points().useActionPoints(5);
        assertDotNotGenerateAction();
    }

    @Test
    void notAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.player(other).cell(125))
        );

        turn.points().useActionPoints(6);
        assertDotNotGenerateAction();
    }

    @Test
    void withAreaSpell() throws SQLException {
        dataSet.pushFunctionalSpells();

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122).spells(223))
            .addEnemy(builder -> builder.player(other).cell(125))
            .addEnemy(builder -> builder.cell(126))
        );

        Cast cast = generateCast();

        assertEquals(223, cast.spell().id());
        assertEquals(96, cast.target().id());

        assertInCastEffectArea(125, 126);
        assertNotInCastEffectArea(122);
    }

    @Test
    void allowAttackAlliesIfMostDamageAreForEnemies() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(145, 5))
            .addAlly(builder -> builder.cell(167))
            .addEnemy(builder -> builder.cell(137))
            .addEnemy(builder -> builder.cell(138))
            .addEnemy(builder -> builder.cell(166))
        );

        removeSpell(3);

        Cast cast = generateCast();

        assertEquals(145, cast.spell().id());
        assertEquals(152, cast.target().id());

        assertInCastEffectArea(152, 167, 137, 138, 166);
    }

    @Test
    void disallowAttackAlliesIfMostDamageAreForAllies() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(145, 5))
            .addAlly(builder -> builder.cell(167))
            .addAlly(builder -> builder.cell(137))
            .addAlly(builder -> builder.cell(138))
            .addEnemy(builder -> builder.cell(166))
        );

        removeSpell(3);

        assertDotNotGenerateAction();
    }

    @Test
    void disallowAttackAlliesIfItKilledMoreAlliesThanEnemies() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(145, 5))
            .addAlly(builder -> builder.cell(167).currentLife(5))
            .addEnemy(builder -> builder.cell(137).currentLife(500))
            .addEnemy(builder -> builder.cell(138).currentLife(500))
            .addEnemy(builder -> builder.cell(166).currentLife(500))
        );

        removeSpell(3);

        assertDotNotGenerateAction();
    }

    @Test
    void allowAttackAlliesIfItKilledMoreEnemies() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(145, 5))
            .addAlly(builder -> builder.cell(167).currentLife(5))
            .addEnemy(builder -> builder.cell(137).currentLife(3))
            .addEnemy(builder -> builder.cell(138).currentLife(4))
            .addEnemy(builder -> builder.cell(166))
        );

        removeSpell(3);

        Cast cast = generateCast();

        assertEquals(145, cast.spell().id());
        assertEquals(152, cast.target().id());
    }

    @Test
    void allowAttackAlliesIfItKilledAtLeastOneEnemy() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(145, 5))
            .addAlly(builder -> builder.cell(167))
            .addEnemy(builder -> builder.cell(137).currentLife(3))
            .addAlly(builder -> builder.cell(138))
            .addAlly(builder -> builder.cell(166))
            .addEnemy(builder -> builder.cell(122))
        );

        removeSpell(3);

        Cast cast = generateCast();

        assertEquals(145, cast.spell().id());
        assertEquals(152, cast.target().id());
    }

    @Test
    void allowAttackAlliesIfItKillTheLastEnemy() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(145, 5))
            .addAlly(builder -> builder.cell(167).currentLife(5))
            .addAlly(builder -> builder.cell(137).currentLife(3))
            .addEnemy(builder -> builder.cell(138).currentLife(4))
        );

        removeSpell(3);

        Cast cast = generateCast();

        assertEquals(145, cast.spell().id());
        assertEquals(152, cast.target().id());
    }

    @Test
    void suicideStrategyAllow() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();
        player.properties().experience().add(10000000000L);

        action = new Attack(container.get(Simulator.class), Attack.SuicideStrategy.ALLOW);

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(157, 5).currentLife(5))
            .addEnemy(builder -> builder.cell(167).currentLife(1000))
            .addEnemy(builder -> builder.cell(137).currentLife(1000))
            .addEnemy(builder -> builder.cell(138).currentLife(1000))
            .addEnemy(builder -> builder.cell(166).currentLife(1000))
        );

        removeSpell(3);

        Cast cast = generateCast();

        assertEquals(157, cast.spell().id());
        assertEquals(137, cast.target().id());
    }

    @Test
    void suicideStrategyDeny() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();
        player.properties().experience().add(10000000000L);

        action = new Attack(container.get(Simulator.class), Attack.SuicideStrategy.DENY);

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(157, 5).currentLife(5))
            .addEnemy(builder -> builder.cell(167).currentLife(1000))
            .addEnemy(builder -> builder.cell(137).currentLife(1000))
            .addEnemy(builder -> builder.cell(138).currentLife(1000))
            .addEnemy(builder -> builder.cell(166).currentLife(1000))
        );

        removeSpell(3);

        assertDotNotGenerateAction();
    }

    @Test
    void suicideStrategyIfKillEnemy() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();
        player.properties().experience().add(10000000000L);

        action = new Attack(container.get(Simulator.class), Attack.SuicideStrategy.IF_KILL_ENEMY);

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(157, 5).currentLife(5))
            .addEnemy(builder -> builder.cell(167).currentLife(1000))
            .addEnemy(builder -> builder.cell(137).currentLife(1000))
            .addEnemy(builder -> builder.cell(138).currentLife(1000))
            .addEnemy(builder -> builder.cell(166).currentLife(1000))
        );

        removeSpell(3);

        assertDotNotGenerateAction();

        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(152).spell(157, 5).currentLife(5))
            .addEnemy(builder -> builder.cell(167).currentLife(5))
            .addEnemy(builder -> builder.cell(137).currentLife(1000))
            .addEnemy(builder -> builder.cell(138).currentLife(1000))
            .addEnemy(builder -> builder.cell(166).currentLife(1000))
        );

        removeSpell(3);

        assertCast(157, 137);
    }

    @Test
    void shouldConsiderBoostOnSameDamage() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dataSet.pushFunctionalSpells();

        configureFight(fb -> fb
            // Divine Sword (145) and Bramble (183) cause same damage, but 145 will also add boost
            .addSelf(builder -> builder.cell(152).spell(183, 5).spell(145, 5).charac(Characteristic.AGILITY, 0).charac(Characteristic.STRENGTH, 0))
            .addEnemy(builder -> builder.cell(167))
        );

        removeSpell(3);

        assertCast(145, 152);

        configureFight(fb -> fb
            // Bramble cause more damage than divide sword because of the strength
            .addSelf(builder -> builder.cell(152).spell(183, 5).spell(145, 5)
                .charac(Characteristic.AGILITY, 0)
                .charac(Characteristic.STRENGTH, 50)
            )
            .addEnemy(builder -> builder.cell(167))
        );

        removeSpell(3);

        assertCast(183, 167);
    }
}
