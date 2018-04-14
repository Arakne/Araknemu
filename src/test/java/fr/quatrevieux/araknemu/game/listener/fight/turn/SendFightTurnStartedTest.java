package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStopped;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FinishTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class SendFightTurnStartedTest extends FightBaseCase {
    private Fight fight;
    private SendFightTurnStarted listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new SendFightTurnStarted(fight);
    }

    @Test
    void onTurnStopped() {
        FightTurn turn = new FightTurn(player.fighter(), fight, Duration.ofSeconds(30));

        listener.on(new TurnStarted(turn));

        requestStack.assertLast(new StartTurn(turn));
    }
}