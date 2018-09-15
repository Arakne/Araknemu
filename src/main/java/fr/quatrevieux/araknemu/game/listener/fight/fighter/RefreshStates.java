package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnTerminated;

/**
 * Refresh the states list on turn terminated
 */
final public class RefreshStates implements Listener<TurnTerminated> {
    @Override
    public void on(TurnTerminated event) {
        event.turn().fighter().states().refresh();
    }

    @Override
    public Class<TurnTerminated> event() {
        return TurnTerminated.class;
    }
}
