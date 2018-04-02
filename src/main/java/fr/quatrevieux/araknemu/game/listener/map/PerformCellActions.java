package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
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
