package fr.quatrevieux.araknemu.game.fight.turn.action.event;

import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;

/**
 * A fight action is started
 */
final public class FightActionStarted {
    final private Action action;
    final private ActionResult result;

    public FightActionStarted(Action action, ActionResult result) {
        this.action = action;
        this.result = result;
    }

    public Action action() {
        return action;
    }

    public ActionResult result() {
        return result;
    }
}
