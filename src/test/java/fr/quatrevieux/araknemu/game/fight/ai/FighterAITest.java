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
import fr.quatrevieux.araknemu.game.fight.ai.action.AiActionFactory;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.GeneratorAggregate;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.memory.MemoryKey;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.FightAction;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FighterAITest extends FightBaseCase {
    private PlayableFighter fighter;
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
        otherEnemy.move(fight.map().get(126));

        fight.nextState();
    }

    @Test
    void getters() {
        FighterAI ai = new FighterAI(fighter, fight, NullGenerator.get());

        assertSame(fighter, ai.fighter());
        assertEquals(enemy, ai.enemy().get());
    }

    @Test
    void memoryGetSet() {
        FighterAI ai = new FighterAI(fighter, fight, NullGenerator.get());

        MemoryKey<Integer> key = new MemoryKey<Integer>() {
            @Override
            public @Nullable Integer defaultValue() {
                return 42;
            }

            @Override
            public @Nullable Integer refresh(Integer value) {
                return null;
            }
        };

        assertEquals(42, ai.get(key));
        ai.set(key, 10);
        assertEquals(10, ai.get(key));
    }

    @Test
    void notStarted() {
        FighterAI ai = new FighterAI(fighter, fight, NullGenerator.get());

        assertThrows(IllegalStateException.class, ai::turn);
        assertThrows(IllegalStateException.class, ai::run);
    }

    @Test
    void enemyShouldFilterDeadFighters() {
        FighterAI ai = new FighterAI(fighter, fight, NullGenerator.get());

        enemy.life().kill(fighter);

        assertEquals(otherEnemy, ai.enemy().get());
    }

    @Test
    void enemyWhenInvokedShouldReturnNearestOfInvoker() {
        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, fight.map().get(112)); // Adjacent to enemy 126
        invoc.init();

        FighterAI ai = new FighterAI(invoc, fight, NullGenerator.get());

        assertEquals(enemy, ai.enemy().get());
    }

    @Test
    void enemyWhenInvokedButInvokerHiddenShouldReturnNearest() {
        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, fight.map().get(112)); // Adjacent to enemy 126
        invoc.init();

        player.fighter().setHidden(player.fighter(), true);

        FighterAI ai = new FighterAI(invoc, fight, NullGenerator.get());

        assertEquals(otherEnemy, ai.enemy().get());
    }

    @Test
    void allyShouldBeEmptyForClassicFighter() {
        FighterAI ai = new FighterAI(fighter, fight, NullGenerator.get());

        assertFalse(ai.ally().isPresent());
    }

    @Test
    void allyShouldInvokerOnInvocationFighter() {
        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, fight.map().get(112)); // Adjacent to enemy 126
        invoc.init();

        FighterAI ai = new FighterAI(invoc, fight, NullGenerator.get());

        assertEquals(player.fighter(), ai.ally().get());
    }

    @Test
    void allyShouldBeEmptyIfInvokerIsHidden() {
        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, fight.map().get(112)); // Adjacent to enemy 126
        invoc.init();

        FighterAI ai = new FighterAI(invoc, fight, NullGenerator.get());

        player.fighter().setHidden(player.fighter(), true);

        assertFalse(ai.ally().isPresent());
    }

    @RepeatedIfExceptionsTest
    void startEmptyShouldStop() throws InterruptedException {
        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, NullGenerator.get());
        ai.start(turn);

        assertFalse(turn.active());
    }

    @RepeatedIfExceptionsTest
    void startUnit() throws InterruptedException {
        ActionGenerator generator1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator generator2 = Mockito.mock(ActionGenerator.class);

        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new GeneratorAggregate(new ActionGenerator[] {generator1, generator2}));

        FightAction action = Mockito.mock(FightAction.class);
        Mockito.when(action.validate(Mockito.any())).thenReturn(true);
        Mockito.when(action.start()).thenReturn(Mockito.mock(ActionResult.class));
        Mockito.when(generator1.generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class))).thenReturn(Optional.of(action));

        ai.start(turn);

        Mockito.verify(generator1).initialize(ai);
        Mockito.verify(generator2).initialize(ai);

        Mockito.verify(generator1).generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class));
        Mockito.verify(generator2, Mockito.never()).generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class));

        assertSame(turn, ai.turn());
        assertTrue(turn.active());
    }

    @RepeatedIfExceptionsTest
    void startWithExceptionShouldStopTurn() throws InterruptedException {
        ActionGenerator generator1 = Mockito.mock(ActionGenerator.class);
        Mockito.when(generator1.generate(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("Test"));

        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new GeneratorAggregate(new ActionGenerator[] {generator1}));

        ai.start(turn);

        Mockito.verify(generator1).initialize(ai);

        Mockito.verify(generator1).generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class));

        assertFalse(turn.active());
    }

    @RepeatedIfExceptionsTest
    void startWithInvalidActionShouldStopTurn() throws InterruptedException {
        ActionGenerator generator1 = Mockito.mock(ActionGenerator.class);
        Mockito.when(generator1.generate(Mockito.any(), Mockito.any())).thenReturn(
            Optional.of(fight.actions().cast().create(
                fighter,
                fighter.spells().get(3),
                fight.map().get(0)
            ))
        );

        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new GeneratorAggregate(new ActionGenerator[] {generator1}));

        ai.start(turn);

        Mockito.verify(generator1).initialize(ai);

        Mockito.verify(generator1).generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class));

        assertFalse(turn.active());
    }

    @RepeatedIfExceptionsTest
    void startShouldCallMemoryRefresh() throws InterruptedException {
        ActionGenerator generator = Mockito.mock(ActionGenerator.class);

        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new GeneratorAggregate(new ActionGenerator[] {generator}));
        MemoryKey<Object> key = Mockito.mock(MemoryKey.class);
        Object value = new Object();

        ai.set(key, value);

        FightAction action = Mockito.mock(FightAction.class);
        Mockito.when(action.validate(Mockito.any())).thenReturn(true);
        Mockito.when(action.start()).thenReturn(Mockito.mock(ActionResult.class));
        Mockito.when(generator.generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class))).thenReturn(Optional.of(action));

        ai.start(turn);

        Mockito.verify(key).refresh(value);
        assertTrue(turn.active());
    }

    @RepeatedIfExceptionsTest
    void startWithoutAvailableActionShouldStopTurn() throws InterruptedException {
        ActionGenerator generator1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator generator2 = Mockito.mock(ActionGenerator.class);

        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new GeneratorAggregate(new ActionGenerator[] {generator1, generator2}));

        Mockito.when(generator1.generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class))).thenReturn(Optional.empty());
        Mockito.when(generator2.generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class))).thenReturn(Optional.empty());

        ai.start(turn);

        Mockito.verify(generator1).initialize(ai);
        Mockito.verify(generator2).initialize(ai);

        Mockito.verify(generator1).generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class));
        Mockito.verify(generator2).generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class));

        assertFalse(turn.active());
    }

    @RepeatedIfExceptionsTest
    void startWithInactiveTurnShouldNotExecuteActions() throws InterruptedException {
        ActionGenerator generator1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator generator2 = Mockito.mock(ActionGenerator.class);

        fight.turnList().start();
        FightTurn turn = fight.turnList().current().get();

        FighterAI ai = new FighterAI(fighter, fight, new GeneratorAggregate(new ActionGenerator[] {generator1, generator2}));

        turn.stop();
        ai.start(turn);

        Mockito.verify(generator1).initialize(ai);
        Mockito.verify(generator2).initialize(ai);

        Mockito.verify(generator1, Mockito.never()).generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class));
        Mockito.verify(generator2, Mockito.never()).generate(Mockito.eq(ai), Mockito.any(AiActionFactory.class));
    }

    @Test
    void runWithoutStart() {
        FighterAI ai = new FighterAI(fighter, fight, NullGenerator.get());

        assertThrows(IllegalStateException.class, ai::run);
    }

    @Test
    void helper() {
        FighterAI ai = new FighterAI(fighter, fight, NullGenerator.get());

        assertInstanceOf(AIHelper.class, ai.helper());
        assertSame(ai.helper(), ai.helper());
    }
}
