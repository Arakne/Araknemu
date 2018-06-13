package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnListChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;

/**
 * Send the new turn list
 */
final public class SendTurnList implements Listener<TurnListChanged> {
    final private Fight fight;

    public SendTurnList(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(TurnListChanged event) {
        fight.send(new FighterTurnOrder(event.turnList()));
    }

    @Override
    public Class<TurnListChanged> event() {
        return TurnListChanged.class;
    }
}
