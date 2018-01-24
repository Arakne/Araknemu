package fr.quatrevieux.araknemu.game.event.listener.map;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.action.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.MapTriggers;

/**
 * Perform cell actions for player move
 */
final public class PerformCellActions implements Listener<PlayerMoveFinished> {
    final private MapTriggers triggers;

    public PerformCellActions(MapTriggers triggers) {
        this.triggers = triggers;
    }

    @Override
    public void on(PlayerMoveFinished event) {
        triggers.perform(event.player());
    }

    @Override
    public Class<PlayerMoveFinished> event() {
        return PlayerMoveFinished.class;
    }
}
