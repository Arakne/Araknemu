package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;

/**
 * Send packets on start turn
 */
final public class SendFightTurnStarted implements Listener<TurnStarted> {
    final private Fight fight;

    public SendFightTurnStarted(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(TurnStarted event) {
        fight.send(new StartTurn(event.turn()));
    }

    @Override
    public Class<TurnStarted> event() {
        return TurnStarted.class;
    }
}
