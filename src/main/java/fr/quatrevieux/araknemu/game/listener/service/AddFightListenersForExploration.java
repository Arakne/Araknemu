package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.game.listener.player.exploration.LeaveExplorationForFight;

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
