package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnTerminated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshStatesTest extends FightBaseCase {
    private Fight fight;
    private RefreshStates listener;
    private PlayerFighter fighter;
    private FightTurn turn;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        listener = new RefreshStates();

        turn = new FightTurn(player.fighter(), fight, Duration.ofMillis(50));
        requestStack.clear();
    }

    @Test
    void onTurnTerminated() {
        fighter.states().push(2);
        fighter.states().push(3, 1);

        listener.on(new TurnTerminated(turn, false));

        assertTrue(fighter.states().has(2));
        assertFalse(fighter.states().has(3));
    }
}
