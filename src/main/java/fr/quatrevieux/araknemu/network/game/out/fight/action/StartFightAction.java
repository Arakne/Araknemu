package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

/**
 * Start a fight action
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L83
 */
final public class StartFightAction {
    final private Action action;

    public StartFightAction(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "GAS" + action.performer().id();
    }
}
