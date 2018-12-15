package fr.quatrevieux.araknemu.game.exploration.event;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.Restrictions;

/**
 * Trigger when exploration player's restrictions has changed
 * Dispatch to map
 */
final public class RestrictionsChanged {
    final private ExplorationPlayer player;
    final private Restrictions restrictions;

    public RestrictionsChanged(ExplorationPlayer player, Restrictions restrictions) {
        this.player = player;
        this.restrictions = restrictions;
    }

    public ExplorationPlayer player() {
        return player;
    }

    public Restrictions restrictions() {
        return restrictions;
    }
}
