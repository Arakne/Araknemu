package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterReadyState;

/**
 * Send the ready state of the fighter
 */
final public class SendFighterReadyState implements Listener<FighterReadyStateChanged> {
    final private Fight fight;

    public SendFighterReadyState(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterReadyStateChanged event) {
        fight.send(new FighterReadyState(event.fighter()));
    }

    @Override
    public Class<FighterReadyStateChanged> event() {
        return FighterReadyStateChanged.class;
    }
}
