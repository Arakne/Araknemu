package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStopped;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FinishTurn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class SendFightTurnStoppedTest extends FightBaseCase {
    private Fight fight;
    private SendFightTurnStopped listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new SendFightTurnStopped(fight);
    }

    @Test
    void onTurnStopped() {
        FightTurn turn = new FightTurn(player.fighter(), fight, Duration.ofSeconds(30));

        listener.on(new TurnStopped(turn));

        requestStack.assertLast(new FinishTurn(turn));
    }
}