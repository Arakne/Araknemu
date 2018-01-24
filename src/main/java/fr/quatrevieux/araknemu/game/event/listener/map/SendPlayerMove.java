package fr.quatrevieux.araknemu.game.event.listener.map;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.action.PlayerMoving;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Send the player move action, when it was validated
 */
final public class SendPlayerMove implements Listener<PlayerMoving> {
    final private ExplorationMap map;

    public SendPlayerMove(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(PlayerMoving event) {
        map.send(
            new GameActionResponse(event.action())
        );
    }

    @Override
    public Class<PlayerMoving> event() {
        return PlayerMoving.class;
    }
}
