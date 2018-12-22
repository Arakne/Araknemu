package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.event.OrientationChanged;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.out.emote.PlayerOrientation;

/**
 * Send to map that a player has changed its orientation
 */
final public class SendPlayerChangeOrientation implements Listener<OrientationChanged> {
    final private ExplorationMap map;

    public SendPlayerChangeOrientation(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(OrientationChanged event) {
        map.send(new PlayerOrientation(event.player()));
    }

    @Override
    public Class<OrientationChanged> event() {
        return OrientationChanged.class;
    }
}
