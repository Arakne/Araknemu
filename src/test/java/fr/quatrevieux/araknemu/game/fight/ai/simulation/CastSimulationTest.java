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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class CastSimulationTest extends FightBaseCase {
    private CastSimulation simulation;
    private Fight fight;
    private Fighter fighter;
    private Fighter allie;
    private Fighter ennemy;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();

        GamePlayer alliePlayer = makeSimpleGamePlayer(10);
        fighter = player.fighter();
        ennemy = other.fighter();

        fight.state(PlacementState.class).joinTeam(allie = new PlayerFighter(alliePlayer), fighter.team());

        simulation = new CastSimulation(Mockito.mock(Spell.class), fighter, fight.map().get(123));
        fight.nextState();
    }

    @Test
    void heal() {
        fighter.life().alter(fighter, -10);
        allie.life().alter(fighter, -10);
        ennemy.life().alter(fighter, -10);

        simulation.alterLife(5, fighter);
        simulation.alterLife(4, allie);
        simulation.alterLife(3, ennemy);

        assertEquals(5, simulation.selfLife());
        assertEquals(4, simulation.alliesLife());
        assertEquals(3, simulation.enemiesLife());
    }

    @Test
    void healLimitByLostLife() {
        fighter.life().alter(fighter, -10);

        simulation.alterLife(100, fighter);

        assertEquals(10, simulation.selfLife());
    }

    @Test
    void addDamage() {
        simulation.addDamage(5, fighter);
        simulation.addDamage(4, allie);
        simulation.addDamage(3, ennemy);

        assertEquals(-5, simulation.selfLife());
        assertEquals(-4, simulation.alliesLife());
        assertEquals(-3, simulation.enemiesLife());
    }

    @Test
    void addDamageLimitByLife() {
        simulation.addDamage(100, ennemy);

        assertEquals(-50, simulation.enemiesLife());
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
        simulation.addDamage(1000, allie);

        assertEquals(1, simulation.killedAllies());
        assertEquals(0, simulation.killedEnemies());

        simulation.addDamage(1000, ennemy);

        assertEquals(1, simulation.killedAllies());
        assertEquals(1, simulation.killedEnemies());
    }

    @Test
    void merge() {
        simulation.addDamage(15, ennemy);
        simulation.addBoost(15, ennemy);

        CastSimulation other = new CastSimulation(Mockito.mock(Spell.class), fighter, fight.map().get(123));

        other.addDamage(25, ennemy);
        other.addBoost(-10, ennemy);

        simulation.merge(other, 20);

        assertEquals(-20, simulation.enemiesLife());
        assertEquals(13, simulation.enemiesBoost());
    }

    @Test
    void mergeKill() {
        simulation.addDamage(15, ennemy);

        CastSimulation other = new CastSimulation(Mockito.mock(Spell.class), fighter, fight.map().get(123));

        other.addDamage(500, ennemy);

        simulation.merge(other, 20);

        assertEquals(1, simulation.killedEnemies());
    }
}
