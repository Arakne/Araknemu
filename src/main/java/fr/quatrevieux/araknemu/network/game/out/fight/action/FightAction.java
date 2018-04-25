package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import org.apache.commons.lang3.StringUtils;

/**
 * Send the fight action
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L127
 */
final public class FightAction {
    final private ActionResult result;

    public FightAction(ActionResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return
            "GA" + (result.success() ? "0" : "") +  ";" +
            result.action() + ";" +
            result.performer().id() + ";" +
            StringUtils.join(result.arguments(), ",")
        ;
    }
}
