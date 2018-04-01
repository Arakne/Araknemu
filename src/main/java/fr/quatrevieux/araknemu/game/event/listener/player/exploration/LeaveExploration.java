package fr.quatrevieux.araknemu.game.event.listener.player.exploration;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Leave exploration actions
 */
final public class LeaveExploration implements Runnable {
    final private ExplorationPlayer player;

    public LeaveExploration(ExplorationPlayer player) {
        this.player = player;
    }

    @Override
    public void run() {
        player.leave();
        player.interactions().stop();
    }
}
