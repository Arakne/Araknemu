package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.event.NextTurnInitiated;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendFightersInformationTest extends FightBaseCase {
    private Fight fight;
    private SendFightersInformation listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new SendFightersInformation(fight);
    }

    @Test
    void onNextTurnInitiated() {
        listener.on(new NextTurnInitiated());

        requestStack.assertLast(
            new TurnMiddle(fight.fighters())
        );
    }
}