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
public final class ActionEffect {
    private final int id;
    private final PassiveFighter caster;
    private final Object[] arguments;

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
    public static ActionEffect usedMovementPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(129, fighter, fighter.id(), -quantity);
    }

    /**
     * Send used action points after an action
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    public static ActionEffect usedActionPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(102, fighter, fighter.id(), -quantity);
    }

    /**
     * Change target life
     *
     * @param caster The effect castet
     * @param target The target
     * @param quantity The life points difference. Negative for damage, Positive for heal
     */
    public static ActionEffect alterLifePoints(PassiveFighter caster, PassiveFighter target, int quantity) {
        return new ActionEffect(100, caster, target.id(), quantity);
    }

    /**
     * Critical hit for spell
     *
     * @param caster The spell caster
     * @param spell The launched spell
     */
    public static ActionEffect criticalHitSpell(PassiveFighter caster, Spell spell) {
        return new ActionEffect(301, caster, spell.id());
    }

    /**
     * Critical hit for close combat cast
     *
     * @param caster The weapon caster
     */
    public static ActionEffect criticalHitCloseCombat(PassiveFighter caster) {
        return new ActionEffect(304, caster);
    }

    /**
     * A fighter die
     *
     * @param caster The spell caster
     * @param fighter The dead fighter
     */
    public static ActionEffect fighterDie(PassiveFighter caster, PassiveFighter fighter) {
        return new ActionEffect(103, caster, fighter.id());
    }

    /**
     * Teleport a fighter
     *
     * @param caster The spell caster
     * @param fighter The teleport fighter
     * @param target The target cell
     */
    public static ActionEffect teleport(PassiveFighter caster, PassiveFighter fighter, FightCell target) {
        return new ActionEffect(4, caster, fighter.id(), target.id());
    }

    /**
     * The fighter skip the next turn
     *
     * @param caster The buff caster
     * @param fighter The target fighter
     */
    public static ActionEffect skipNextTurn(PassiveFighter caster, PassiveFighter fighter) {
        return new ActionEffect(140, caster, fighter.id());
    }

    /**
     * The fighter return the spell
     *
     * @param fighter The fighter
     * @param success true is the spell is successfully returned
     */
    public static ActionEffect returnSpell(PassiveFighter fighter, boolean success) {
        return new ActionEffect(106, fighter, fighter.id(), success ? "1" : "0");
    }

    /**
     * Buff effect for characteristic change
     *
     * @param buff The applied buff
     */
    public static ActionEffect buff(Buff buff, int value) {
        return new ActionEffect(buff.effect().effect(), buff.caster(), buff.target().id(), value, buff.effect().duration());
    }

    /**
     * Boost the sight of the target for a given duration
     *
     * @param caster Spell caster
     * @param target Target
     * @param value Boosted value
     * @param duration Effect duration
     */
    public static ActionEffect boostSight(PassiveFighter caster, PassiveFighter target, int value, int duration) {
        return new ActionEffect(117, caster, target.id(), value, duration);
    }

    /**
     * Decrease the sight of the target for a given duration
     *
     * @param caster Spell caster
     * @param target Target
     * @param value Decrease value
     * @param duration Effect duration
     */
    public static ActionEffect decreaseSight(PassiveFighter caster, PassiveFighter target, int value, int duration) {
        return new ActionEffect(116, caster, target.id(), value, duration);
    }

    /**
     * Add action points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    public static ActionEffect addActionPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(120, fighter, fighter.id(), quantity);
    }

    /**
     * Remove action points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    public static ActionEffect removeActionPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(168, fighter, fighter.id(), -quantity);
    }

    /**
     * Add movement points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The MP quantity
     */
    public static ActionEffect addMovementPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(128, fighter, fighter.id(), quantity);
    }

    /**
     * Remove movement points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The MP quantity
     */
    public static ActionEffect removeMovementPoints(PassiveFighter fighter, int quantity) {
        return new ActionEffect(169, fighter, fighter.id(), -quantity);
    }

    /**
     * Suffered damage value is reduced
     *
     * @param fighter The fighter
     * @param value The reduction value
     */
    public static ActionEffect reducedDamage(PassiveFighter fighter, int value) {
        return new ActionEffect(105, fighter, fighter.id(), value);
    }

    /**
     * Add state to the fighter
     *
     * @param fighter The fighter
     * @param state The state id to add
     */
    public static ActionEffect addState(PassiveFighter fighter, int state) {
        return new ActionEffect(950, fighter, fighter.id(), state, 1);
    }

    /**
     * Remove state from the fighter
     *
     * @param fighter The fighter
     * @param state The state id to remove
     */
    public static ActionEffect removeState(PassiveFighter fighter, int state) {
        return new ActionEffect(950, fighter, fighter.id(), state, 0);
    }

    /**
     * Remove all buffs that can be removed from the target
     * 
     * @param caster The caster
     * @param target The target
     */
    public static ActionEffect dispelBuffs(PassiveFighter caster, PassiveFighter target) {
        return new ActionEffect(132, caster, target.id());
    }

    /**
     * The fighter has been slided (i.e. move back or front) by the caster
     *
     * @param caster The spell caster
     * @param target The target (which has moved)
     * @param destination The destination cell
     */
    public static ActionEffect slide(PassiveFighter caster, PassiveFighter target, FightCell destination) {
        return new ActionEffect(5, caster, target.id(), destination.id());
    }

    /**
     * Damage as been reflected by the target
     *
     * @param castTarget The original cast target
     * @param value Reflected value
     */
    public static ActionEffect reflectedDamage(PassiveFighter castTarget, int value) {
        return new ActionEffect(107, castTarget, castTarget.id(), value);
    }

    /**
     * The target has dodged the lost of action points
     *
     * @param caster The spell caster
     * @param target The target (which has dodged point lost)
     * @param value The dodged point list value
     */
    public static ActionEffect dodgeActionPointLost(PassiveFighter caster, PassiveFighter target, int value) {
        return new ActionEffect(308, caster, target.id(), value);
    }

    /**
     * The target has dodged the lost of movement points
     *
     * @param caster The spell caster
     * @param target The target (which has dodged point lost)
     * @param value The dodged point list value
     */
    public static ActionEffect dodgeMovementPointLost(PassiveFighter caster, PassiveFighter target, int value) {
        return new ActionEffect(309, caster, target.id(), value);
    }

    /**
     * Change the appearance of the target
     *
     * @param caster The spell caster
     * @param target The effect target
     * @param newAppearance The new appearance id (can be found in `clips/sprites/[id].swf)
     * @param duration The effect duration
     */
    public static ActionEffect changeAppearance(PassiveFighter caster, PassiveFighter target, int newAppearance, int duration) {
        return new ActionEffect(149, caster, target.id(), target.sprite().gfxId(), newAppearance, duration);
    }

    /**
     * Reset the appearance of the target
     */
    public static ActionEffect resetAppearance(PassiveFighter caster, PassiveFighter target) {
        final int baseGfxId = target.sprite().gfxId();

        return new ActionEffect(149, caster, target.id(), baseGfxId, baseGfxId, 0);
    }

    /**
     * Launch visual effect of a spell
     *
     * @param caster The visual effect caster
     * @param targetCell The target cell
     * @param spell Spell which contains sprite arguments
     */
    public static ActionEffect launchVisualEffect(PassiveFighter caster, FightCell targetCell, Spell spell) {
        return new ActionEffect(208, caster, targetCell.id(), spell.spriteId(), spell.spriteArgs(), spell.level());
    }

    /**
     * The fighter has been make invisible
     *
     * @param caster Spell caster
     * @param target Effect target
     */
    public static ActionEffect fighterHidden(PassiveFighter caster, PassiveFighter target) {
        return new ActionEffect(150, caster, target.id(), 1);
    }

    /**
     * The invisibility effect is terminated
     *
     * @param caster Spell caster
     * @param target Effect target
     */
    public static ActionEffect fighterVisible(PassiveFighter caster, PassiveFighter target) {
        return new ActionEffect(150, caster, target.id(), 0);
    }

    /**
     * Add an invoked creature to the fight
     *
     * @param caster Invoker
     * @param invocation Invocation to add
     */
    public static ActionEffect addInvocation(PassiveFighter caster, PassiveFighter invocation) {
        return new ActionEffect(181, caster, "+" + invocation.sprite());
    }

    /**
     * Send a custom packet, but queued on the caster sequencer
     *
     * @param caster Effect caster
     * @param packet Packet to send
     */
    public static ActionEffect packet(PassiveFighter caster, Object packet) {
        return new ActionEffect(999, caster, packet);
    }
}
