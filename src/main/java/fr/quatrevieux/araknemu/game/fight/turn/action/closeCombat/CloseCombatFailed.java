package fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;

/**
 * Result for critical failure for weapon cast
 */
final public class CloseCombatFailed implements ActionResult {
    final private Fighter caster;

    public CloseCombatFailed(Fighter caster) {
        this.caster = caster;
    }

    @Override
    public int action() {
        return 305;
    }

    @Override
    public Fighter performer() {
        return caster;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {};
    }

    @Override
    public boolean success() {
        return false;
    }
}
