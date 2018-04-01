package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.common.Disconnected;
import fr.quatrevieux.araknemu.game.event.listener.player.exploration.LeaveExploration;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Stop exploration on disconnect
 */
final public class StopExploration implements Listener<Disconnected> {
    final private ExplorationPlayer player;

    public StopExploration(ExplorationPlayer player) {
        this.player = player;
    }

    @Override
    public void on(Disconnected event) {
        new LeaveExploration(player).run();
    }

    @Override
    public Class<Disconnected> event() {
        return Disconnected.class;
    }
}
