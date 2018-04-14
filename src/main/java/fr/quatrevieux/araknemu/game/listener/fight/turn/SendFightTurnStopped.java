package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStopped;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FinishTurn;

/**
 * Send that a fighter turn is terminated
 */
final public class SendFightTurnStopped implements Listener<TurnStopped> {
    final private Fight fight;

    public SendFightTurnStopped(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(TurnStopped event) {
        fight.send(new FinishTurn(event.turn()));
    }

    @Override
    public Class<TurnStopped> event() {
        return TurnStopped.class;
    }
}
