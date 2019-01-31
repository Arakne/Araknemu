package fr.quatrevieux.araknemu.game.listener.player.exploration;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;

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
        player.player().stop(player);
    }

    @Override
    public Class<FightJoined> event() {
        return FightJoined.class;
    }
}
