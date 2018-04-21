package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoving;

/**
 * Validate move path : stop the player when a non-walkable cell is found
 */
final public class ValidatePlayerPath implements Listener<PlayerMoving> {
    @Override
    public void on(PlayerMoving event) {
        event.action().replace(
            event.action().path().keepWhile(step -> step.cell().walkable())
        );
    }

    @Override
    public Class<PlayerMoving> event() {
        return PlayerMoving.class;
    }
}
