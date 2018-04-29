package fr.quatrevieux.araknemu.game.exploration.interaction.action;

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
    void pushFirstWillRunAndSetBusy() {
        BlockingAction action = Mockito.mock(BlockingAction.class);

        queue.push(action);

        Mockito.verify(action).start();
        assertTrue(queue.isBusy());
    }

    @Test
    void pushWillSetIdOnTheFirst() {
        BlockingAction a1 = Mockito.mock(BlockingAction.class);
        BlockingAction a2 = Mockito.mock(BlockingAction.class);
        BlockingAction a3 = Mockito.mock(BlockingAction.class);

        queue.push(a1);
        queue.push(a2);
        queue.push(a3);

        Mockito.verify(a1).setId(1);
        Mockito.verify(a2, Mockito.never()).setId(Mockito.anyInt());
        Mockito.verify(a3, Mockito.never()).setId(Mockito.anyInt());
    }

    @Test
    void pushTwoWillStartOnlyTheFirstOnBlocking() {
        BlockingAction a1 = Mockito.mock(BlockingAction.class);
        BlockingAction a2 = Mockito.mock(BlockingAction.class);

        queue.push(a1);
        queue.push(a2);

        Mockito.verify(a1).start();
        Mockito.verify(a2, Mockito.never()).start();

        assertTrue(queue.isBusy());
    }

    @Test
    void endWithOneBlockingActionWillTerminateTheCurrentActionAndClearTheQueue() {
        BlockingAction action = Mockito.mock(BlockingAction.class);
        Mockito.when(action.id()).thenReturn(1);

        queue.push(action);
        queue.end(1);

        Mockito.verify(action).end();
        assertFalse(queue.isBusy());
    }

    @Test
    void endWithTwoActionWillStartTheNextAction() {
        BlockingAction a1 = Mockito.mock(BlockingAction.class);
        BlockingAction a2 = Mockito.mock(BlockingAction.class);
        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);
        queue.push(a2);

        queue.end(1);

        Mockito.verify(a1).end();
        Mockito.verify(a2).start();

        assertTrue(queue.isBusy());
    }

    @Test
    void startWithExceptionWillRemoveTheAction() {
        BlockingAction action = Mockito.mock(BlockingAction.class);

        Mockito.doThrow(new IllegalArgumentException()).when(action).start();

        assertThrows(IllegalArgumentException.class, () -> queue.push(action));
        assertFalse(queue.isBusy());
    }

    @Test
    void startWithExceptionWillExecuteTheNextAction() {
        BlockingAction a1 = Mockito.mock(BlockingAction.class);
        BlockingAction a2 = Mockito.mock(BlockingAction.class);
        BlockingAction a3 = Mockito.mock(BlockingAction.class);
        Mockito.when(a1.id()).thenReturn(1);
        Mockito.when(a3.id()).thenReturn(3);

        Mockito.doThrow(new IllegalArgumentException()).when(a2).start();

        queue.push(a1);
        queue.push(a2);
        queue.push(a3);

        assertThrows(IllegalArgumentException.class, () -> queue.end(1));

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
    void endWithBadId() {
        BlockingAction a1 = Mockito.mock(BlockingAction.class);
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
        BlockingAction a1 = Mockito.mock(BlockingAction.class);
        Mockito.when(a1.id()).thenReturn(1);

        queue.push(a1);

        assertThrows(NoSuchElementException.class, () -> queue.cancel(15, "arg"), "The action ID do not corresponds");
    }

    @Test
    void cancelWillClearTheQueue() {
        BlockingAction a1 = Mockito.mock(BlockingAction.class);
        BlockingAction a2 = Mockito.mock(BlockingAction.class);
        BlockingAction a3 = Mockito.mock(BlockingAction.class);

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

        Mockito.verify(a1).start();
        Mockito.verify(a2).start();
        Mockito.verify(a3).start();

        assertFalse(queue.isBusy());
    }

    @Test
    void nonBlockingActionAfterBlockingWillBeExecuteAtEnd() {
        BlockingAction a1 = Mockito.mock(BlockingAction.class);
        Action a2 = Mockito.mock(Action.class);

        queue.push(a1);
        Mockito.verify(a1).start();

        queue.push(a2);
        queue.end(a1.id());

        Mockito.verify(a1).end();
        Mockito.verify(a2).start();

        assertFalse(queue.isBusy());
    }

    @Test
    void stopWithoutActions() {
        queue.stop();
    }

    @Test
    void stopWithOnlyOneCurrentAction() {
        BlockingAction a1 = Mockito.mock(BlockingAction.class);

        queue.push(a1);

        queue.stop();

        assertFalse(queue.isBusy());
        Mockito.verify(a1).cancel(null);
    }

    @Test
    void stopWithPendingAction() {
        BlockingAction a1 = Mockito.mock(BlockingAction.class);
        Action a2 = Mockito.mock(Action.class);

        queue.push(a1);
        queue.push(a2);

        queue.stop();

        assertFalse(queue.isBusy());
        Mockito.verify(a1).cancel(null);
    }
}
