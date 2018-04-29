package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.TriggerCell;

/**
 * Perform cell actions for player move
 */
final public class PerformCellActions implements Listener<PlayerMoveFinished> {
    @Override
    public void on(PlayerMoveFinished event) {
        if (event.cell() instanceof TriggerCell) {
            TriggerCell.class.cast(event.cell()).onStop(event.player());
        }
    }

    @Override
    public Class<PlayerMoveFinished> event() {
        return PlayerMoveFinished.class;
    }
}
