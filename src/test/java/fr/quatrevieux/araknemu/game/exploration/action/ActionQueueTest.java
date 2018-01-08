package fr.quatrevieux.araknemu.game.exploration.action;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ActionQueueTest extends GameBaseCase {
    private ActionQueue queue;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        queue = new ActionQueue();
    }

    @Test
    void generateId() {
        assertEquals(1, queue.generateId());
        assertEquals(2, queue.generateId());
    }

    @Test
    void pushFirstWillRunAndSetBusy() throws Exception {
        Action action = Mockito.mock(Action.class);

        queue.push(action);

        Mockito.verify(action).start();
        assertTrue(queue.isBusy());
    }

    @Test
    void pushTwoWillStartOnlyTheFirst() throws Exception {
        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);

        queue.push(a1);
        queue.push(a2);

        Mockito.verify(a1).start();
        Mockito.verify(a2, Mockito.never()).start();

        assertTrue(queue.isBusy());
    }

    @Test
    void endWithOneActionWillTerminateTheCurrentActionAndClearTheQueue() throws Exception {
        Action action = Mockito.mock(Action.class);
        Mockito.when(action.id()).thenReturn(1);

        queue.push(action);
        queue.end(1);

        Mockito.verify(action).end();
        assertFalse(queue.isBusy());
    }

    @Test
    void endWithTwoActionWillStartTheNextAction() throws Exception {
        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);
        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);
        queue.push(a2);

        queue.end(1);

        Mockito.verify(a1).end();
        Mockito.verify(a2).start();

        assertTrue(queue.isBusy());
    }

    @Test
    void startWithExceptionWillRemoveTheAction() throws Exception {
        Action action = Mockito.mock(Action.class);

        Mockito.doThrow(new Exception("my error")).when(action).start();

        assertThrows(Exception.class, () -> queue.push(action), "my error");
        assertFalse(queue.isBusy());
    }

    @Test
    void startWithExceptionWillExecuteTheNextAction() throws Exception {
        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);
        Action a3 = Mockito.mock(Action.class);
        Mockito.when(a1.id()).thenReturn(1);
        Mockito.when(a3.id()).thenReturn(3);

        Mockito.doThrow(new Exception("my error")).when(a2).start();

        queue.push(a1);
        queue.push(a2);
        queue.push(a3);

        assertThrows(Exception.class, () -> queue.end(1), "my error");

        Mockito.verify(a3).start();

        queue.end(3);

        Mockito.verify(a3).end();

        assertFalse(queue.isBusy());
    }

    @Test
    void endWithoutAction() {
        assertThrows(NoSuchElementException.class, () -> queue.end(1));
    }

    @Test
    void endWithBadId() throws Exception {
        Action a1 = Mockito.mock(Action.class);
        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);

        assertThrows(NoSuchElementException.class, () -> queue.end(15), "The action ID do not corresponds");
    }

    @Test
    void cancelWithoutAction() {
        assertThrows(NoSuchElementException.class, () -> queue.cancel(1, "arg"));
    }

    @Test
    void cancelWithBadId() throws Exception {
        Action a1 = Mockito.mock(Action.class);
        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);

        assertThrows(NoSuchElementException.class, () -> queue.cancel(15, "arg"), "The action ID do not corresponds");
    }

    @Test
    void cancelWillClearTheQueue() throws Exception {
        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);
        Action a3 = Mockito.mock(Action.class);

        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);
        queue.push(a2);
        queue.push(a3);

        queue.cancel(1, "arg");

        Mockito.verify(a1).cancel("arg");

        assertFalse(queue.isBusy());
    }
}
