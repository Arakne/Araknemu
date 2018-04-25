package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.apache.commons.lang3.StringUtils;

/**
 * Effect of a fight action
 */
final public class ActionEffect {
    final private int id;
    final private Fighter caster;
    final private Object[] arguments;

    public ActionEffect(int id, Fighter caster, Object... arguments) {
        this.id = id;
        this.caster = caster;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "GA;" + id + ";" + caster.id() + ";" + StringUtils.join(arguments, ",");
    }

    /**
     * Send used movement points after a movement
     *
     * @param fighter The fighter
     * @param quantity The MP quantity
     */
    static public ActionEffect usedMovementPoints(Fighter fighter, int quantity) {
        return new ActionEffect(129, fighter, fighter.id(), -quantity);
    }
}
