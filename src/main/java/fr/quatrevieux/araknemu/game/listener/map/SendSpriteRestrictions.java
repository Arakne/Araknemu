package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.event.RestrictionsChanged;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;

import java.util.Collections;

/**
 * Send the sprite restrictions when changed
 */
final public class SendSpriteRestrictions implements Listener<RestrictionsChanged> {
    final private ExplorationMap map;

    public SendSpriteRestrictions(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(RestrictionsChanged event) {
        map.send(new AddSprites(Collections.singleton(event.player().sprite())));
    }

    @Override
    public Class<RestrictionsChanged> event() {
        return RestrictionsChanged.class;
    }
}
