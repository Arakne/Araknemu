package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.event.CellChanged;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;

import java.util.Collections;

/**
 * Send to all map that a player has changed map
 */
final public class SendPlayerChangeCell implements Listener<CellChanged> {
    final private ExplorationMap map;

    public SendPlayerChangeCell(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(CellChanged event) {
        map.send(
            new AddSprites(
                Collections.singleton(event.player().sprite())
            )
        );
    }

    @Override
    public Class<CellChanged> event() {
        return CellChanged.class;
    }
}
