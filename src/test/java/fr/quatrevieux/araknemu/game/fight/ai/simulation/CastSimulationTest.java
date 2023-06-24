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

package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CastSimulationTest extends FightBaseCase {
    private CastSimulation simulation;
    private Fight fight;
    private Fighter fighter;
    private Fighter allie;
    private Fighter ennemy;
    private Spell spell;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();

        GamePlayer alliePlayer = makeSimpleGamePlayer(10);
        fighter = player.fighter();
        ennemy = other.fighter();

        fight.state(PlacementState.class).joinTeam(allie = new PlayerFighter(alliePlayer), fighter.team());

        simulation = new CastSimulation(spell = Mockito.mock(Spell.class), fighter, fight.map().get(123));
        fight.nextState();
    }

    @Test
    void heal() {
        fighter.life().alter(fighter, -10);
        allie.life().alter(fighter, -10);
        ennemy.life().alter(fighter, -10);

        simulation.addHeal(new Interval(5, 5), fighter);
        simulation.addHeal(new Interval(4, 4), allie);
        simulation.addHeal(new Interval(3, 3), ennemy);

        assertEquals(5, simulation.selfLife());
        assertEquals(4, simulation.alliesLife());
        assertEquals(3, simulation.enemiesLife());
    }

    @ParameterizedTest
    @MethodSource("provideHeal")
    void healLimitByLostLife(Interval value, double expectedValue) {
        fighter.life().alter(fighter, -10);

        simulation.addHeal(value, fighter);

        assertEquals(expectedValue, simulation.selfLife(), 0.1);
    }

    public static Stream<Arguments> provideHeal() {
        return Stream.of(
            Arguments.of(new Interval(100, 100), 10.0),
            Arguments.of(new Interval(1, 10), 5.5),
            Arguments.of(new Interval(5, 20), 9.17),
            Arguments.of(new Interval(5, 15), 8.75),
            Arguments.of(new Interval(1, 5), 3)
        );
    }

    @Test
    void addDamage() {
        simulation.addDamage(new Interval(5, 5), fighter);
        simulation.addDamage(new Interval(4, 4), allie);
        simulation.addDamage(new Interval(3, 3), ennemy);

        assertEquals(-5, simulation.selfLife());
        assertEquals(-4, simulation.alliesLife());
        assertEquals(-3, simulation.enemiesLife());
    }

    @Test
    void addDamageLimitByLife() {
        simulation.addDamage(new Interval(100, 100), ennemy);

        assertEquals(-50, simulation.enemiesLife());
    }

    @ParameterizedTest
    @MethodSource("providePoison")
    void addPoison(Interval damage, int duration, double expectedDamage) {
        // life = 50
        simulation.addPoison(damage, duration, allie);

        assertEquals(0.0, simulation.killedAllies());
        assertEquals(-expectedDamage, simulation.alliesLife(), 0.1);
        assertEquals(0, simulation.killedEnemies());
    }

    private static Stream<Arguments> providePoison() {
        return Stream.of(
            Arguments.of(new Interval(1, 10), 3, 12.38),
            Arguments.of(new Interval(1, 10), 5, 20.6),
            Arguments.of(new Interval(1, 100), 2, 33.14),
            Arguments.of(new Interval(100, 200), 2, 37.5),
            Arguments.of(new Interval(1, 2), 5, 5.6)
        );
    }

    @Test
    void addBoost() {
        simulation.addBoost(5, fighter);
        simulation.addBoost(4, allie);
        simulation.addBoost(3, ennemy);

        assertEquals(5, simulation.selfBoost());
        assertEquals(4, simulation.alliesBoost());
        assertEquals(3, simulation.enemiesBoost());
    }

    @Test
    void killDamage() {
        simulation.addDamage(new Interval(1000, 1000), allie);

        assertEquals(1, simulation.killedAllies());
        assertEquals(0, simulation.killedEnemies());
        assertEquals(0, simulation.suicideProbability());

        simulation.addDamage(new Interval(1000, 1000), ennemy);

        assertEquals(1, simulation.killedAllies());
        assertEquals(1, simulation.killedEnemies());
        assertEquals(0, simulation.suicideProbability());

        simulation.addDamage(new Interval(1000, 1000), fighter);

        assertEquals(1, simulation.killedAllies());
        assertEquals(1, simulation.killedEnemies());
        assertEquals(1, simulation.suicideProbability());
    }

    @ParameterizedTest
    @MethodSource("provideKillChanceDamage")
    void killChance(Interval damage, double chance, double expectedDamage) {
        // life = 50
        simulation.addDamage(damage, allie);

        assertEquals(chance, simulation.killedAllies(), 0.1);
        assertEquals(-expectedDamage, simulation.alliesLife(), 0.1);
        assertEquals(0, simulation.killedEnemies());
    }

    private static Stream<Arguments> provideKillChanceDamage() {
        return Stream.of(
            Arguments.of(new Interval(1, 100), 0.5, 37.8), // 50% * 25 + 50% * 50
            Arguments.of(new Interval(1, 200), 0.75, 43.9), // 25% * 25 + 75% * 50
            Arguments.of(new Interval(1, 75), 0.33, 33.7), // 66% * 25 + 33% * 50
            Arguments.of(new Interval(25, 100), 0.66, 45.8), // 33% * 37.5 + 66% * 50
            Arguments.of(new Interval(1, 51), 0.02, 26),
            Arguments.of(new Interval(1, 49), 0.0, 25.0),
            Arguments.of(new Interval(50, 60), 1.0, 50.0),
            Arguments.of(new Interval(1000, 2000), 1.0, 50.0)
        );
    }

    @Test
    void suicide() {
        simulation.addDamage(new Interval(1000, 1000), fighter);

        assertEquals(0, simulation.killedAllies());
        assertEquals(1, simulation.suicideProbability());
        assertEquals(0, simulation.killedEnemies());
    }

    @Test
    void merge() {
        simulation.addDamage(new Interval(15, 15), ennemy);
        simulation.addBoost(15, ennemy);

        CastSimulation other = new CastSimulation(Mockito.mock(Spell.class), fighter, fight.map().get(123));

        other.addDamage(new Interval(25, 25), ennemy);
        other.addBoost(-10, ennemy);

        simulation.merge(other, 20);

        assertEquals(-20, simulation.enemiesLife());
        assertEquals(13, simulation.enemiesBoost());
    }

    @Test
    void mergeKill() {
        simulation.addDamage(new Interval(15, 15), ennemy);

        CastSimulation other = new CastSimulation(Mockito.mock(Spell.class), fighter, fight.map().get(123));

        other.addDamage(new Interval(500, 500), ennemy);

        simulation.merge(other, 20);

        assertEquals(.2, simulation.killedEnemies());
    }

    @Test
    void mergeSuicide() {
        simulation.addDamage(new Interval(0, 500), fighter);
        assertEquals(0.41, simulation.suicideProbability());

        CastSimulation other = new CastSimulation(Mockito.mock(Spell.class), fighter, fight.map().get(123));

        other.addDamage(new Interval(100, 300), fighter);
        assertEquals(0.025, other.suicideProbability());

        simulation.merge(other, 20);

        assertEquals(.415, simulation.suicideProbability());
    }

    @Test
    void alterActionPoints() {
        Mockito.when(spell.apCost()).thenReturn(3);
        assertEquals(3, simulation.actionPointsCost());

        simulation.alterActionPoints(1);
        assertEquals(2, simulation.actionPointsCost());

        CastSimulation other = new CastSimulation(spell, fighter, fight.map().get(123));
        other.alterActionPoints(2);

        simulation.merge(other, 25);

        assertEquals(1.5, simulation.actionPointsCost());
    }

    @Test
    void addHealBuff() {
        fighter.life().alter(fighter, -10);
        simulation.addHealBuff(new Interval(5, 10), 3, fighter);

        assertEquals(7.5, simulation.selfLife());
        assertEquals(11.25, simulation.selfBoost());
    }

    @Test
    void addHealBuffAlreadyFullLife() {
        simulation.addHealBuff(new Interval(5, 10), 3, fighter);

        assertEquals(0, simulation.selfLife());
        assertEquals(11.25, simulation.selfBoost());
    }

    @Test
    void addHealBuffOnlyOneTurnShouldNotSetAsBuff() {
        fighter.life().alter(fighter, -10);
        simulation.addHealBuff(new Interval(5, 10), 1, fighter);

        assertEquals(7.5, simulation.selfLife());
        assertEquals(0, simulation.selfBoost());
    }
}
