package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.game.event.listener.map.SendAccessories;

/**
 * Register listeners for exploration
 */
final public class AddExplorationListeners implements Listener<StartExploration> {
    @Override
    public void on(StartExploration event) {
        event.player().dispatcher().add(new SendAccessories(event.player()));
    }

    @Override
    public Class<StartExploration> event() {
        return StartExploration.class;
    }
}
