package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class StartFightActionTest {
    @Test
    void generate() {
        Action action = Mockito.mock(Action.class);
        Fighter fighter = Mockito.mock(Fighter.class);

        Mockito.when(action.performer()).thenReturn(fighter);
        Mockito.when(fighter.id()).thenReturn(123);

        assertEquals("GAS123", new StartFightAction(action).toString());
    }
}