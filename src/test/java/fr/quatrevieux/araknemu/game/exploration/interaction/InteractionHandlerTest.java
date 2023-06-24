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

package fr.quatrevieux.araknemu.game.exploration.interaction;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionHandlerTest extends TestCase {
    private InteractionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new InteractionHandler();
    }

    @Test
    void interacting() {
        assertFalse(handler.interacting());
        assertFalse(handler.busy());

        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        handler.start(interaction);

        assertTrue(handler.interacting());
        assertTrue(handler.busy());
    }

    @Test
    void busyOneBlockingAction() throws Exception {
        assertFalse(handler.busy());

        handler.push(new MyBlockingAction());

        assertTrue(handler.busy());
    }

    @Test
    void startAlreadyInteracting() {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        handler.start(interaction);
        assertThrows(IllegalStateException.class, () -> handler.start(interaction));
    }

    @Test
    void stopNotInteracting() {
        handler.stop();
    }

    @Test
    void stopInteracting() {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        handler.start(interaction);
        handler.stop();

        Mockito.verify(interaction).stop();

        assertFalse(handler.interacting());
    }

    @Test
    void stopWillStopGameActions() throws Exception {
        BlockingAction current = Mockito.spy(MyBlockingAction.class);

        handler.push(current);
        handler.push(new MyBlockingAction());

        handler.stop();

        assertFalse(handler.busy());
        Mockito.verify(current).cancel(null);
    }

    @Test
    void getNoInteraction() {
        assertThrows(IllegalArgumentException.class, () -> handler.get(Interaction.class));
    }

    @Test
    void getBadType() {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        handler.start(interaction);

        assertThrows(IllegalArgumentException.class, () -> handler.get(ExtendedInteraction.class));
    }

    @Test
    void getSuccess() {
        ExtendedInteraction interaction = Mockito.mock(ExtendedInteraction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        handler.start(interaction);

        assertSame(interaction, handler.get(ExtendedInteraction.class));
    }

    @Test
    void removeNotInteracting() {
        assertThrows(IllegalStateException.class, () -> handler.remove());
    }

    @Test
    void removeSuccess() {
        ExtendedInteraction interaction = Mockito.mock(ExtendedInteraction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        handler.start(interaction);

        assertSame(interaction, handler.remove());
    }

    @Test
    void pushBlockingActionWhenInteractingWillRaiseError() {
        ExtendedInteraction interaction = Mockito.mock(ExtendedInteraction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        handler.start(interaction);

        assertThrows(IllegalStateException.class, () -> handler.push(Mockito.mock(BlockingAction.class)));
    }

    @Test
    void pushNonBlockingActionWhenInteractingWillExecuteAction() throws Exception {
        ExtendedInteraction interaction = Mockito.mock(ExtendedInteraction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        handler.start(interaction);

        Action action = Mockito.mock(Action.class);
        handler.push(action);

        Mockito.verify(action).start(Mockito.any(ActionQueue.class));
    }

    interface ExtendedInteraction extends Interaction {}

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