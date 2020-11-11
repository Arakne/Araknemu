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

package fr.quatrevieux.araknemu.game.fight.ai;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FighterAITest extends FightBaseCase {
    private Fighter fighter;
    private Fight fight;

    private Fighter enemy;
    private Fighter otherEnemy;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();

        fighter = player.fighter();
        enemy = other.fighter();

        otherEnemy = new PlayerFighter(makeSimpleGamePlayer(10));

        fight.state(PlacementState.class).joinTeam(otherEnemy, enemy.team());
        fight.nextState();
    }

    @Test
    void getters() {
        FighterAI ai = new FighterAI(fighter, fight, new ActionGenerator[0]);

        assertSame(fighter, ai.fighter());
        assertSame(enemy, ai.enemy().get());
    }

    @Test
    void enemyShouldFilterDeadFighters() {
        FighterAI ai = new FighterAI(fighter, fight, new ActionGenerator[0]);

        enemy.life().kill(fighter);

        assertSame(otherEnemy, ai.enemy().get());
    }

    @RepeatedIfExceptionsTest
    void startEmptyShouldStop() throws InterruptedException {
        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new ActionGenerator[0]);
        ai.start(turn);

        Thread.sleep(20);

        assertFalse(turn.active());
    }

    @RepeatedIfExceptionsTest
    void startUnit() throws InterruptedException {
        ActionGenerator generator1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator generator2 = Mockito.mock(ActionGenerator.class);

        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new ActionGenerator[] {generator1, generator2});

        Mockito.when(generator1.generate(ai)).thenReturn(Optional.of(Mockito.mock(Action.class)));

        ai.start(turn);

        Mockito.verify(generator1).initialize(ai);
        Mockito.verify(generator2).initialize(ai);

        Thread.sleep(20);

        Mockito.verify(generator1).generate(ai);
        Mockito.verify(generator2, Mockito.never()).generate(ai);

        assertSame(turn, ai.turn());
        assertTrue(turn.active());
    }

    @RepeatedIfExceptionsTest
    void startWithoutAvailableActionShouldStopTurn() throws InterruptedException {
        ActionGenerator generator1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator generator2 = Mockito.mock(ActionGenerator.class);

        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new ActionGenerator[] {generator1, generator2});

        Mockito.when(generator1.generate(ai)).thenReturn(Optional.empty());
        Mockito.when(generator2.generate(ai)).thenReturn(Optional.empty());

        ai.start(turn);

        Mockito.verify(generator1).initialize(ai);
        Mockito.verify(generator2).initialize(ai);

        Thread.sleep(20);

        Mockito.verify(generator1).generate(ai);
        Mockito.verify(generator2).generate(ai);

        assertFalse(turn.active());
    }

    @RepeatedIfExceptionsTest
    void startWithInactiveTurnShouldNotExecuteActions() throws InterruptedException {
        ActionGenerator generator1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator generator2 = Mockito.mock(ActionGenerator.class);

        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new ActionGenerator[] {generator1, generator2});

        turn.stop();
        ai.start(turn);

        Mockito.verify(generator1).initialize(ai);
        Mockito.verify(generator2).initialize(ai);

        Thread.sleep(20);

        Mockito.verify(generator1, Mockito.never()).generate(ai);
        Mockito.verify(generator2, Mockito.never()).generate(ai);
    }

    @Test
    void runWithoutStart() {
        FighterAI ai = new FighterAI(fighter, fight, new ActionGenerator[] {});

        assertThrows(IllegalStateException.class, ai::run);
    }
}
