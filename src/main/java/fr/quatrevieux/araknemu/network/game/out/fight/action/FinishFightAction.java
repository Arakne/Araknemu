package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

/**
 * Finish the current fight action
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L99
 */
final public class FinishFightAction {
    final private Action action;

    public FinishFightAction(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "GAF" + action.type().end() + "|" + action.performer().id();
    }
}
