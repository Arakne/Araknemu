package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.NextTurnInitiated;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;

/**
 * Send the fighters information between two turns
 */
final public class SendFightersInformation implements Listener<NextTurnInitiated> {
    final private Fight fight;

    public SendFightersInformation(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(NextTurnInitiated event) {
        fight.send(new TurnMiddle(fight.fighters()));
    }

    @Override
    public Class<NextTurnInitiated> event() {
        return NextTurnInitiated.class;
    }
}
