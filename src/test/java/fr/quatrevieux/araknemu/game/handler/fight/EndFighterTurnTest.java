package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.network.game.in.fight.TurnEnd;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FinishTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndFighterTurnTest extends FightBaseCase {
    private EndFighterTurn handler;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new EndFighterTurn();
        fight = createFight();
    }

    @Test
    void notActiveTurn() {
        assertThrows(FightException.class, () -> handler.handle(session, new TurnEnd()));
    }

    @Test
    void success() {
        fight.state(PlacementState.class).startFight();
        fight.turnList().start();
        requestStack.clear();

        FightTurn turn = player.fighter().turn();

        handler.handle(session, new TurnEnd());

        assertSame(other.fighter(), fight.turnList().current().get().fighter());
        assertThrows(FightException.class, () -> player.fighter().turn());

        requestStack.assertAll(
            new FinishTurn(turn),
            new TurnMiddle(fight.fighters()),
            new StartTurn(other.fighter().turn())
        );
    }
}
