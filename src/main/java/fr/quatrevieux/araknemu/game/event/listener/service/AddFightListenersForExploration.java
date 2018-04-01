package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.event.listener.player.exploration.LeaveExplorationForFight;

/**
 * Add fight listeners on exploration player
 */
final public class AddFightListenersForExploration implements Listener<ExplorationPlayerCreated> {
    @Override
    public void on(ExplorationPlayerCreated event) {
        event.player().dispatcher().add(new LeaveExplorationForFight(event.player()));
    }

    @Override
    public Class<ExplorationPlayerCreated> event() {
        return ExplorationPlayerCreated.class;
    }
}
