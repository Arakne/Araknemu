package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;

/**
 * Add listeners for map on exploration
 */
final public class AddExplorationMapListeners implements Listener<ExplorationPlayerCreated> {
    @Override
    public void on(ExplorationPlayerCreated event) {
        event.player().dispatcher().add(new SendMapData(event.player()));
    }

    @Override
    public Class<ExplorationPlayerCreated> event() {
        return ExplorationPlayerCreated.class;
    }
}
