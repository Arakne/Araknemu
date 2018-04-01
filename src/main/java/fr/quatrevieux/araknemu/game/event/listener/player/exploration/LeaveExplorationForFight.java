package fr.quatrevieux.araknemu.game.event.listener.player.exploration;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.fight.FightJoined;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Stop exploration when join fight
 */
final public class LeaveExplorationForFight implements Listener<FightJoined> {
    final private ExplorationPlayer player;

    public LeaveExplorationForFight(ExplorationPlayer player) {
        this.player = player;
    }

    @Override
    public void on(FightJoined event) {
        new LeaveExploration(player).run();

        player.stopExploring();
    }

    @Override
    public Class<FightJoined> event() {
        return FightJoined.class;
    }
}
