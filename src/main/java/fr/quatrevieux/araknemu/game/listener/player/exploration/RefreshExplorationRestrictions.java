package fr.quatrevieux.araknemu.game.listener.player.exploration;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.event.RestrictionsChanged;

/**
 * Refresh the exploration restrictions when locale restrictions changed
 */
final public class RefreshExplorationRestrictions implements Listener<RestrictionsChanged> {
    final private ExplorationPlayer exploration;

    public RefreshExplorationRestrictions(ExplorationPlayer exploration) {
        this.exploration = exploration;
    }

    @Override
    public void on(RestrictionsChanged event) {
        exploration.restrictions().refresh();
    }

    @Override
    public Class<RestrictionsChanged> event() {
        return RestrictionsChanged.class;
    }
}
