package fr.quatrevieux.araknemu.game.fight.turn.action.event;

import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

/**
 * A fight action is terminated
 */
final public class FightActionTerminated {
    final private Action action;

    public FightActionTerminated(Action action) {
        this.action = action;
    }

    public Action action() {
        return action;
    }
}
