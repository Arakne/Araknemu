package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class FinishFightActionTest {
    @Test
    void generateMove() {
        Action action = Mockito.mock(Action.class);
        Fighter fighter = Mockito.mock(Fighter.class);

        Mockito.when(action.performer()).thenReturn(fighter);
        Mockito.when(action.type()).thenReturn(ActionType.MOVE);
        Mockito.when(fighter.id()).thenReturn(123);

        assertEquals("GAF2|123", new FinishFightAction(action).toString());
    }

    @Test
    void generateOther() {
        Action action = Mockito.mock(Action.class);
        Fighter fighter = Mockito.mock(Fighter.class);

        Mockito.when(action.performer()).thenReturn(fighter);
        Mockito.when(action.type()).thenReturn(ActionType.CAST);
        Mockito.when(fighter.id()).thenReturn(123);

        assertEquals("GAF0|123", new FinishFightAction(action).toString());
    }
}