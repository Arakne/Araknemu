/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.apache.commons.lang3.StringUtils;

/**
 * Effect of a fight action
 */
final public class ActionEffect {
    final private int id;
    final private PassiveFighter caster;
    final private Object[] arguments;

    public ActionEffect(int id, PassiveFighter caster, Object... arguments) {
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
    static public ActionEffect usedMovementPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(129, fighter, fighter.id(), -quantity);
    }

    /**
     * Send used action points after an action
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    static public ActionEffect usedActionPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(102, fighter, fighter.id(), -quantity);
    }

    /**
     * Change target life
     *
     * @param caster The effect castet
     * @param target The target
     * @param quantity The life points difference. Negative for damage, Positive for heal
     */
    static public ActionEffect alterLifePoints(PassiveFighter caster, PassiveFighter target, int quantity) {
        return new ActionEffect(100, caster, target.id(), quantity);
    }

    /**
     * Critical hit for spell
     *
     * @param caster The spell caster
     * @param spell The launched spell
     */
    static public ActionEffect criticalHitSpell(PassiveFighter caster, Spell spell) {
        return new ActionEffect(301, caster, spell.id());
    }

    /**
     * Critical hit for close combat cast
     *
     * @param caster The weapon caster
     */
    static public ActionEffect criticalHitCloseCombat(PassiveFighter caster) {
        return new ActionEffect(304, caster);
    }

    /**
     * A fighter die
     *
     * @param caster The spell caster
     * @param fighter The dead fighter
     */
    static public ActionEffect fighterDie(PassiveFighter caster, PassiveFighter fighter) {
        return new ActionEffect(103, caster, fighter.id());
    }

    /**
     * Teleport a fighter
     *
     * @param caster The spell caster
     * @param fighter The teleport fighter
     * @param target The target cell
     */
    static public ActionEffect teleport(PassiveFighter caster, PassiveFighter fighter, FightCell target) {
        return new ActionEffect(4, caster, fighter.id(), target.id());
    }

    /**
     * The fighter skip the next turn
     *
     * @param caster The buff caster
     * @param fighter The target fighter
     */
    static public ActionEffect skipNextTurn(PassiveFighter caster, PassiveFighter fighter) {
        return new ActionEffect(140, caster, fighter.id());
    }

    /**
     * The fighter return the spell
     *
     * @param fighter The fighter
     * @param success true is the spell is successfully returned
     */
    static public ActionEffect returnSpell(PassiveFighter fighter, boolean success) {
        return new ActionEffect(106, fighter, fighter.id(), success ? "1" : "0");
    }

    /**
     * Buff effect for characteristic change
     *
     * @param buff The applied buff
     */
    static public ActionEffect buff(Buff buff, int value) {
        return new ActionEffect(buff.effect().effect(), buff.caster(), buff.target().id(), value, buff.effect().duration());
    }

    /**
     * Add action points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    static public ActionEffect addActionPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(120, fighter, fighter.id(), quantity);
    }

    /**
     * Remove action points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    static public ActionEffect removeActionPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(168, fighter, fighter.id(), -quantity);
    }

    /**
     * Add movement points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The MP quantity
     */
    static public ActionEffect addMovementPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(128, fighter, fighter.id(), quantity);
    }

    /**
     * Remove movement points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The MP quantity
     */
    static public ActionEffect removeMovementPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(169, fighter, fighter.id(), -quantity);
    }

    /**
     * Suffered damage value is reduced
     *
     * @param fighter The fighter
     * @param value The reduction value
     */
    static public ActionEffect reducedDamage(PassiveFighter fighter, int value) {
        return new ActionEffect(105, fighter, fighter.id(), value);
    }

    /**
     * Add state to the fighter
     *
     * @param fighter The fighter
     * @param state The state id to add
     */
    static public ActionEffect addState(PassiveFighter fighter, int state) {
        return new ActionEffect(950, fighter, fighter.id(), state, 1);
    }

    /**
     * Remove state from the fighter
     *
     * @param fighter The fighter
     * @param state The state id to remove
     */
    static public ActionEffect removeState(PassiveFighter fighter, int state) {
        return new ActionEffect(950, fighter, fighter.id(), state, 0);
    }

    /**
     * Remove all buffs that can be removed from the target
     * 
     * @param caster The caster
     * @param targer The target
     */
    static public ActionEffect debuff(PassiveFighter caster, PassiveFighter target) {
        return new ActionEffect(132, caster, target.id());
    }
}
