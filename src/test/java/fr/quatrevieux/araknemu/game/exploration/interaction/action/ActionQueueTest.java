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

package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionQueueTest extends GameBaseCase {
    private ActionQueue queue;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        queue = new ActionQueue();
    }

    @Test
    void pushFirstWillRunAndSetBusy() {
        BlockingAction action = Mockito.spy(MyBlockingAction.class);

        queue.push(action);

        Mockito.verify(action).start(queue);
        assertTrue(queue.isBusy());
    }

    @Test
    void pushWillSetIdOnTheFirst() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);
        BlockingAction a2 = Mockito.spy(MyBlockingAction.class);
        BlockingAction a3 = Mockito.spy(MyBlockingAction.class);

        queue.push(a1);
        queue.push(a2);
        queue.push(a3);

        Mockito.verify(a1).setId(1);
        Mockito.verify(a2, Mockito.never()).setId(Mockito.anyInt());
        Mockito.verify(a3, Mockito.never()).setId(Mockito.anyInt());
    }

    @Test
    void pushTwoWillStartOnlyTheFirstOnBlocking() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);
        BlockingAction a2 = Mockito.spy(MyBlockingAction.class);

        queue.push(a1);
        queue.push(a2);

        Mockito.verify(a1).start(queue);
        Mockito.verify(a2, Mockito.never()).start(queue);

        assertTrue(queue.isBusy());
    }

    @Test
    void endWithOneBlockingActionWillTerminateTheCurrentActionAndClearTheQueue() {
        BlockingAction action = Mockito.spy(MyBlockingAction.class);
        Mockito.when(action.id()).thenReturn(1);

        queue.push(action);
        queue.end(1);

        Mockito.verify(action).end();
        assertFalse(queue.isBusy());
    }

    @Test
    void endWithTwoActionWillStartTheNextAction() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);
        BlockingAction a2 = Mockito.spy(MyBlockingAction.class);
        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);
        queue.push(a2);

        queue.end(1);

        Mockito.verify(a1).end();
        Mockito.verify(a2).start(queue);

        assertTrue(queue.isBusy());
    }

    @Test
    void startWithExceptionWillRemoveTheAction() {
        BlockingAction action = Mockito.spy(MyBlockingAction.class);

        Mockito.doThrow(new IllegalArgumentException()).when(action).start(queue);

        assertThrows(IllegalArgumentException.class, () -> queue.push(action));
        assertFalse(queue.isBusy());
    }

    @Test
    void startWithExceptionWillExecuteTheNextAction() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);
        BlockingAction a2 = Mockito.spy(MyBlockingAction.class);
        BlockingAction a3 = Mockito.spy(MyBlockingAction.class);
        Mockito.when(a1.id()).thenReturn(1);
        Mockito.when(a3.id()).thenReturn(3);

        Mockito.doThrow(new IllegalArgumentException()).when(a2).start(queue);

        queue.push(a1);
        queue.push(a2);
        queue.push(a3);

        assertThrows(IllegalArgumentException.class, () -> queue.end(1));

        Mockito.verify(a3).start(queue);

        queue.end(3);

        Mockito.verify(a3).end();

        assertFalse(queue.isBusy());
    }

    @Test
    void endWithoutAction() {
        assertThrows(NoSuchElementException.class, () -> queue.end(1));
    }

    @Test
    void endWithBadId() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);
        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);

        assertThrows(NoSuchElementException.class, () -> queue.end(15), "The action ID do not corresponds");
    }

    @Test
    void cancelWithoutAction() {
        assertThrows(NoSuchElementException.class, () -> queue.cancel(1, "arg"));
    }

    @Test
    void cancelWithBadId() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);
        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);

        assertThrows(NoSuchElementException.class, () -> queue.cancel(15, "arg"), "The action ID do not corresponds");
    }

    @Test
    void cancelWillClearTheQueue() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);
        BlockingAction a2 = Mockito.spy(MyBlockingAction.class);
        BlockingAction a3 = Mockito.spy(MyBlockingAction.class);

        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);
        queue.push(a2);
        queue.push(a3);

        queue.cancel(1, "arg");

        Mockito.verify(a1).cancel("arg");

        assertFalse(queue.isBusy());
    }

    @Test
    void pushNonBlockingActionWillExecuteAll() {
        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);
        Action a3 = Mockito.mock(Action.class);

        queue.push(a1);
        queue.push(a2);
        queue.push(a3);

        Mockito.verify(a1).start(queue);
        Mockito.verify(a2).start(queue);
        Mockito.verify(a3).start(queue);

        assertFalse(queue.isBusy());
    }

    @Test
    void nonBlockingActionAfterBlockingWillBeExecuteAtEnd() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);
        Action a2 = Mockito.mock(Action.class);

        queue.push(a1);
        Mockito.verify(a1).start(queue);

        queue.push(a2);
        queue.end(a1.id());

        Mockito.verify(a1).end();
        Mockito.verify(a2).start(queue);

        assertFalse(queue.isBusy());
    }

    @Test
    void stopWithoutActions() {
        queue.stop();
    }

    @Test
    void stopWithOnlyOneCurrentAction() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);

        queue.push(a1);

        queue.stop();

        assertFalse(queue.isBusy());
        Mockito.verify(a1).cancel(null);
    }

    @Test
    void stopWithPendingAction() {
        BlockingAction a1 = Mockito.spy(MyBlockingAction.class);
        Action a2 = Mockito.mock(Action.class);

        queue.push(a1);
        queue.push(a2);

        queue.stop();

        assertFalse(queue.isBusy());
        Mockito.verify(a1).cancel(null);
    }

    public static class MyBlockingAction implements BlockingAction {
        private int id;

        @Override
        public void cancel(String argument) { }

        @Override
        public void end() { }

        @Override
        public int id() {
            return id;
        }

        @Override
        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void start(ActionQueue queue) {
            queue.setPending(this);
        }

        @Override
        public ExplorationPlayer performer() {
            return null;
        }

        @Override
        public ActionType type() {
            return null;
        }

        @Override
        public Object[] arguments() {
            return new Object[0];
        }
    }
}
