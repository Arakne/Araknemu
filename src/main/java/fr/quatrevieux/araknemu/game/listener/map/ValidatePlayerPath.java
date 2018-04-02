package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoving;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.world.map.PathStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate move path : stop the player when a non-walkable cell is found
 */
final public class ValidatePlayerPath implements Listener<PlayerMoving> {
    final private ExplorationMap map;

    public ValidatePlayerPath(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(PlayerMoving event) {
        List<PathStep> validated = new ArrayList<>(event.action().path().size());

        for (PathStep step : event.action().path()) {
            if (!map.cell(step.cell()).isWalkable()) {
                break;
            }

            validated.add(step);
        }

        event.action().replace(validated);
    }

    @Override
    public Class<PlayerMoving> event() {
        return PlayerMoving.class;
    }
}
