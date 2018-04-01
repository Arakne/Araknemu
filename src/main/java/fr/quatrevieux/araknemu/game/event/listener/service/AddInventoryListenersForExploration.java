package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.game.event.listener.map.SendAccessories;

/**
 * Register listeners for  inventory during exploration
 */
final public class AddInventoryListenersForExploration implements Listener<ExplorationPlayerCreated> {
    @Override
    public void on(ExplorationPlayerCreated event) {
        event.player().dispatcher().add(new SendAccessories(event.player()));
    }

    @Override
    public Class<ExplorationPlayerCreated> event() {
        return ExplorationPlayerCreated.class;
    }
}
