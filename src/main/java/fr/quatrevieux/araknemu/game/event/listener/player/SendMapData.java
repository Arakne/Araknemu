package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.MapLoaded;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;

/**
 * Send the map data to the player when map is loaded
 */
final public class SendMapData implements Listener<MapLoaded> {
    final private GamePlayer player;

    public SendMapData(GamePlayer player) {
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
