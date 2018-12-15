package fr.quatrevieux.araknemu.game.player.event;

import fr.quatrevieux.araknemu.game.player.Restrictions;

/**
 * The restrictions has changed
 */
final public class RestrictionsChanged {
    final private Restrictions restrictions;

    public RestrictionsChanged(Restrictions restrictions) {
        this.restrictions = restrictions;
    }

    public Restrictions restrictions() {
        return restrictions;
    }
}
