package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.event.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;

/**
 * Send the map data to the player when map is loaded
 */
final public class SendMapData implements Listener<MapLoaded> {
    final private ExplorationPlayer player;

    public SendMapData(ExplorationPlayer player) {
        this.player = player;
    }

    @Override
    public void on(MapLoaded event) {
        player.send(new MapData(event.map()));
    }

    @Override
    public Class<MapLoaded> event() {
        return MapLoaded.class;
    }
}
