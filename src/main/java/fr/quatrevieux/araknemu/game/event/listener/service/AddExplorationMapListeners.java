package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.event.listener.player.SendMapData;

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
