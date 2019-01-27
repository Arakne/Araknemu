package fr.quatrevieux.araknemu.game.exploration.npc.dialog;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest extends GameBaseCase {
    @Test
    void id() {
        assertEquals(123, new Response(123, new ArrayList<>()).id());
    }

    @Test
    void checkWithoutAction() throws SQLException, ContainerException {
        assertFalse(new Response(123, new ArrayList<>()).check(explorationPlayer()));
    }

    @Test
    void checkInvalid() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);

        Mockito.when(a1.check(player)).thenReturn(true);
        Mockito.when(a2.check(player)).thenReturn(false);

        assertFalse(new Response(123, Arrays.asList(a1, a2)).check(explorationPlayer()));
    }

    @Test
    void checkSuccess() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);

        Mockito.when(a1.check(player)).thenReturn(true);
        Mockito.when(a2.check(player)).thenReturn(true);

        assertTrue(new Response(123, Arrays.asList(a1, a2)).check(explorationPlayer()));
    }

    @Test
    void apply() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);

        new Response(123, Arrays.asList(a1, a2)).apply(player);

        Mockito.verify(a1).apply(player);
        Mockito.verify(a2).apply(player);
    }
}
