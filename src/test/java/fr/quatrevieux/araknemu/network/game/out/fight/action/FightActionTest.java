package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class FightActionTest {
    @Test
    void generateOnSuccess() {
        assertEquals(
            "GA0;1;123;arg",
            new FightAction(
                new ActionResult() {
                    @Override
                    public int action() {
                        return 1;
                    }

                    @Override
                    public Fighter performer() {
                        Fighter fighter = Mockito.mock(Fighter.class);
                        Mockito.when(fighter.id()).thenReturn(123);

                        return fighter;
                    }

                    @Override
                    public Object[] arguments() {
                        return new Object[] {"arg"};
                    }

                    @Override
                    public boolean success() {
                        return true;
                    }
                }
            ).toString()
        );
    }

    @Test
    void generateOnFail() {
        assertEquals(
            "GA;1;123;arg",
            new FightAction(
                new ActionResult() {
                    @Override
                    public int action() {
                        return 1;
                    }

                    @Override
                    public Fighter performer() {
                        Fighter fighter = Mockito.mock(Fighter.class);
                        Mockito.when(fighter.id()).thenReturn(123);

                        return fighter;
                    }

                    @Override
                    public Object[] arguments() {
                        return new Object[] {"arg"};
                    }

                    @Override
                    public boolean success() {
                        return false;
                    }
                }
            ).toString()
        );
    }
}