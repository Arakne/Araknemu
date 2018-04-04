package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class FighterReadyStateTest {
    @Test
    void notReady() {
        Fighter fighter = Mockito.mock(Fighter.class);

        Mockito.when(fighter.ready()).thenReturn(false);
        Mockito.when(fighter.id()).thenReturn(5);

        assertEquals("GR05", new FighterReadyState(fighter).toString());
    }

    @Test
    void ready() {
        Fighter fighter = Mockito.mock(Fighter.class);

        Mockito.when(fighter.ready()).thenReturn(true);
        Mockito.when(fighter.id()).thenReturn(5);

        assertEquals("GR15", new FighterReadyState(fighter).toString());
    }
}