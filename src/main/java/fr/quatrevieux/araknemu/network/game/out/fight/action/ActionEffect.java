package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
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

    /**
     * Send used action points after an action
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    static public ActionEffect usedActionPoints(Fighter fighter, int quantity) {
        return new ActionEffect(102, fighter, fighter.id(), -quantity);
    }

    /**
     * Change target life
     *
     * @param caster The effect castet
     * @param target The target
     * @param quantity The life points difference. Negative for damage, Positive for heal
     */
    static public ActionEffect alterLifePoints(Fighter caster, Fighter target, int quantity) {
        return new ActionEffect(100, caster, target.id(), quantity);
    }

    /**
     * Critical hit for spell
     *
     * @param caster The spell caster
     * @param spell The launched spell
     */
    static public ActionEffect criticalHitSpell(Fighter caster, Spell spell) {
        return new ActionEffect(301, caster, spell.id());
    }

    /**
     * Critical hit for close combat cast
     *
     * @param caster The weapon caster
     */
    static public ActionEffect criticalHitCloseCombat(Fighter caster) {
        return new ActionEffect(304, caster);
    }

    /**
     * A fighter die
     *
     * @param caster The spell caster
     * @param fighter The dead fighter
     */
    static public ActionEffect fighterDie(Fighter caster, Fighter fighter) {
        return new ActionEffect(103, caster, fighter.id());
    }

    /**
     * Teleport a fighter
     *
     * @param caster The spell caster
     * @param fighter The teleport fighter
     * @param target The target cell
     */
    static public ActionEffect teleport(Fighter caster, Fighter fighter, FightCell target) {
        return new ActionEffect(4, caster, fighter.id(), target.id());
    }

    /**
     * The fighter skip the next turn
     *
     * @param caster The buff caster
     * @param fighter The target fighter
     */
    static public ActionEffect skipNextTurn(Fighter caster, Fighter fighter) {
        return new ActionEffect(140, caster, fighter.id());
    }

    /**
     * The fighter return the spell
     *
     * @param fighter The fighter
     * @param success true is the spell is successfully returned
     */
    static public ActionEffect returnSpell(Fighter fighter, boolean success) {
        return new ActionEffect(106, fighter, fighter.id(), success ? "1" : "0");
    }
}
