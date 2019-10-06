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

package fr.quatrevieux.araknemu.game.fight.turn.action;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionStarted;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionTerminated;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ActionHandlerTest extends FightBaseCase {
    private Fight fight;
    private ActionHandler actionHandler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        actionHandler = new ActionHandler(fight);
    }

    @Test
    void startInvalid() {
        assertFalse(actionHandler.start(new Action() {
            @Override
            public boolean validate() {
                return false;
            }

            @Override
            public ActionResult start() {
                return null;
            }

            @Override
            public Fighter performer() {
                return null;
            }

            @Override
            public ActionType type() {
                return null;
            }

            @Override
            public void end() {

            }

            @Override
            public void failed() {

            }

            @Override
            public Duration duration() {
                return null;
            }
        }));
    }

    @Test
    void startSuccessWillDispatchEvent() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ZERO);

        Mockito.when(result.success()).thenReturn(true);

        AtomicReference<FightActionStarted> ref = new AtomicReference<>();
        fight.dispatcher().add(FightActionStarted.class, ref::set);

        assertTrue(actionHandler.start(action));
        assertSame(action, ref.get().action());
        assertSame(result, ref.get().result());
    }

    @Test
    void startFailedWillDispatchEventAndCallFailed() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);

        Mockito.when(result.success()).thenReturn(false);

        AtomicReference<FightActionStarted> ref = new AtomicReference<>();
        fight.dispatcher().add(FightActionStarted.class, ref::set);

        assertTrue(actionHandler.start(action));
        assertSame(action, ref.get().action());
        assertSame(result, ref.get().result());

        Mockito.verify(action, Mockito.never()).duration();
        Mockito.verify(action).failed();
    }

    @RepeatedIfExceptionsTest
    void startSuccessTerminateActionWhenDurationIsReached() throws InterruptedException {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(100));

        Mockito.when(result.success()).thenReturn(true);

        assertTrue(actionHandler.start(action));

        Mockito.verify(action, Mockito.never()).end();

        Thread.sleep(150);
        Mockito.verify(action, Mockito.times(1)).end();
    }

    @Test
    void startWithPendingAction() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(1000));

        Mockito.when(result.success()).thenReturn(true);

        assertTrue(actionHandler.start(action));

        Action other = Mockito.mock(Action.class);

        assertFalse(actionHandler.start(other));
        Mockito.verify(other, Mockito.never()).start();
    }

    @Test
    void terminateWithoutPendingAction() {
        AtomicReference<FightActionTerminated> ref = new AtomicReference<>();
        fight.dispatcher().add(FightActionTerminated.class, ref::set);

        actionHandler.terminate();

        assertNull(ref.get());
    }

    @Test
    void terminateSuccessWillDispatchEvent() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(1000));

        Mockito.when(result.success()).thenReturn(true);

        actionHandler.start(action);

        AtomicReference<FightActionTerminated> ref = new AtomicReference<>();
        fight.dispatcher().add(FightActionTerminated.class, ref::set);

        actionHandler.terminate();

        Mockito.verify(action).end();
        assertSame(action, ref.get().action());
    }

    @RepeatedIfExceptionsTest
    void terminateSuccessWillCancelTimer() throws InterruptedException {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(200));

        Mockito.when(result.success()).thenReturn(true);

        actionHandler.start(action);

        actionHandler.terminate();

        Thread.sleep(250);
        Mockito.verify(action, Mockito.times(1)).end();
    }

    @Test
    void terminateWithTerminationListener() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(1000));

        Mockito.when(result.success()).thenReturn(true);

        actionHandler.start(action);

        AtomicBoolean b = new AtomicBoolean();
        actionHandler.terminated(() -> b.set(true));
        assertFalse(b.get());

        actionHandler.terminate();

        assertTrue(b.get());
    }

    @Test
    void terminateWithException() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(1000));
        Mockito.doThrow(new RuntimeException()).when(action).end();

        Mockito.when(result.success()).thenReturn(true);

        actionHandler.start(action);

        AtomicBoolean b = new AtomicBoolean();
        actionHandler.terminated(() -> b.set(true));

        assertThrows(Exception.class, () -> actionHandler.terminate());

        assertTrue(b.get());
    }

    @Test
    void terminateWithTerminationListenerWillRemoveOldListeners() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(1000));

        Mockito.when(result.success()).thenReturn(true);

        actionHandler.start(action);

        AtomicInteger i = new AtomicInteger();
        actionHandler.terminated(() -> i.incrementAndGet());
        assertEquals(0, i.get());

        actionHandler.terminate();

        actionHandler.start(action);
        actionHandler.terminate();

        assertEquals(1, i.get());
    }

    @Test
    void terminatedWithoutPendingAction() {
        AtomicBoolean b = new AtomicBoolean();
        actionHandler.terminated(() -> b.set(true));
        assertTrue(b.get());
    }
}
