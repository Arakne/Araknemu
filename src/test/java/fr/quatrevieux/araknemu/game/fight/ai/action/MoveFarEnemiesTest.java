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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.ChainAiFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MoveFarEnemiesTest extends FightBaseCase {
    private Fighter fighter;
    private Fight fight;

    private Fighter enemy;
    private Fighter otherEnemy;

    private MoveFarEnemies action;
    private FighterAI ai;

    private FightTurn turn;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.register(new AiModule(new ChainAiFactory()));
        fight.register(new CommonEffectsModule(fight));

        fighter = player.fighter();
        enemy = other.fighter();

        otherEnemy = new PlayerFighter(makeSimpleGamePlayer(10));

        fight.state(PlacementState.class).joinTeam(otherEnemy, enemy.team());
        fight.nextState();

        fight.turnList().start();

        action = new MoveFarEnemies();

        ai = new FighterAI(fighter, fight, new ActionGenerator[] { new DummyGenerator() });
        ai.start(turn = fight.turnList().current().get());
        action.initialize(ai);
    }

    @Test
    void success() {
        assertEquals(6, distance(enemy));
        assertEquals(8, distance(otherEnemy));

        Optional<Action> result = action.generate(ai);

        assertTrue(result.isPresent());
        assertInstanceOf(Move.class, result.get());

        turn.perform(result.get());
        turn.terminate();

        assertEquals(77, fighter.cell().id());
        assertEquals(0, turn.points().movementPoints());

        assertEquals(9, distance(enemy));
        assertEquals(11, distance(otherEnemy));
    }

    @Test
    void noMP() {
        turn.points().useMovementPoints(3);

        Optional<Action> result = action.generate(ai);

        assertFalse(result.isPresent());
    }

    @Test
    void blocked() {
        fighter.move(fight.map().get(384));
        enemy.move(fight.map().get(370));

        Optional<Action> result = action.generate(ai);

        assertFalse(result.isPresent());
    }

    @Test
    void noNearestAccessibleCells() {
        fighter.move(fight.map().get(384));
        enemy.move(fight.map().get(342));

        Optional<Action> result = action.generate(ai);

        assertFalse(result.isPresent());
    }

    private int distance(Fighter enemy) {
        return fighter.cell().coordinate().distance(enemy.cell());
    }
}
