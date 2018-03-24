package fr.quatrevieux.araknemu.game.exploration.interaction;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

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

    interface ExtendedInteraction extends Interaction {}
}