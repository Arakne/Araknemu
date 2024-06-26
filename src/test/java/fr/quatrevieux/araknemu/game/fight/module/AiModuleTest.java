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

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.memory.MemoryKey;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import groovy.util.logging.Log;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiModuleTest extends FightBaseCase {
    @Test
    void fighterInitialized() throws Exception {
        Fight fight = createPvmFight();

        fight.register(new AiModule(container.get(AiFactory.class), fight, Mockito.mock(Logger.class)));
        fight.nextState();

        for (Fighter monster : fight.team(1).fighters()) {
            assertInstanceOf(FighterAI.class, monster.attachment(FighterAI.class));
        }

        assertNull(player.fighter().attachment(FighterAI.class));
    }

    @Test
    void turnStartedWithoutAi() throws Exception {
        Fight fight = createPvmFight();
        fight.register(new AiModule(container.get(AiFactory.class), fight, Mockito.mock(Logger.class)));
        fight.nextState();
        fight.turnList().start();

        FightTurn turn = fight.turnList().current().get();

        assertSame(player.fighter(), turn.fighter());
        assertTrue(turn.active());

        // Check if there is no pending action
        Runnable action = Mockito.mock(Runnable.class);
        turn.later(action);

        Mockito.verify(action).run();
    }

    @RepeatedIfExceptionsTest
    void turnStartedWithAi() throws Exception {
        Fight fight = createPvmFight();
        fight.register(new AiModule(container.get(AiFactory.class), fight, Mockito.mock(Logger.class)));
        fight.nextState();
        fight.turnList().start();
        requestStack.clear();
        fight.turnList().current().get().stop();

        FightTurn turn = fight.turnList().current().get();

        assertTrue(turn.active());

        // Cast action started : retour de flamme
        requestStack.assertOne("GA0;300;-2;215,126,110,4,10,1,1");

        turn.terminate();
        Thread.sleep(900); // Wait for the AI to start next action

        // Move action started
        requestStack.assertOne("GA0;1;-2;ab-fbGdbU");
    }

    @RepeatedIfExceptionsTest
    void exceptionOnTurnStartShouldStopTheTurn() throws Exception {
        Logger logger = Mockito.mock(Logger.class);

        Fight fight = createPvmFight();
        fight.register(new AiModule(container.get(AiFactory.class), fight, logger));
        fight.nextState();
        fight.turnList().start();

        // The AI will throw an exception on start
        Fighter fighter = fight.turnList().fighters().get(1);
        fighter.attachment(FighterAI.class).set(new MemoryKey<Object>() {
            @Override
            public @Nullable Object refresh(Object value) {
                throw new RuntimeException("error");
            }
        }, true);

        requestStack.clear();
        fight.turnList().current().get().stop();

        Mockito.verify(logger).debug("Starting AI for {}", fighter);
        Mockito.verify(logger).error(Mockito.eq("Error during AI execution. Stop the turn."), Mockito.any(RuntimeException.class));

        assertEquals(fight.turnList().fighters().get(2), fight.turnList().current().get().fighter());
    }
}
