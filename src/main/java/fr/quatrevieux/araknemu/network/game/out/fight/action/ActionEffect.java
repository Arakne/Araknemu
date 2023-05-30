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
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.apache.commons.lang3.StringUtils;

/**
 * Effect of a fight action
 */
public final class ActionEffect {
    private final int id;
    private final FighterData caster;
    private final Object[] arguments;

    public ActionEffect(int id, FighterData caster, Object... arguments) {
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
    public static ActionEffect usedMovementPoints(FighterData fighter, int quantity) {
        return new ActionEffect(129, fighter, fighter.id(), -quantity);
    }

    /**
     * Send used action points after an action
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    public static ActionEffect usedActionPoints(FighterData fighter, int quantity) {
        return new ActionEffect(102, fighter, fighter.id(), -quantity);
    }

    /**
     * Change target life
     *
     * @param caster The effect castet
     * @param target The target
     * @param quantity The life points difference. Negative for damage, Positive for heal
     */
    public static ActionEffect alterLifePoints(FighterData caster, FighterData target, int quantity) {
        return new ActionEffect(100, caster, target.id(), quantity);
    }

    /**
     * Critical hit for spell
     *
     * @param caster The spell caster
     * @param spell The launched spell
     */
    public static ActionEffect criticalHitSpell(FighterData caster, Spell spell) {
        return new ActionEffect(301, caster, spell.id());
    }

    /**
     * Critical hit for close combat cast
     *
     * @param caster The weapon caster
     */
    public static ActionEffect criticalHitCloseCombat(FighterData caster) {
        return new ActionEffect(304, caster);
    }

    /**
     * A fighter die
     *
     * @param caster The spell caster
     * @param fighter The dead fighter
     */
    public static ActionEffect fighterDie(FighterData caster, FighterData fighter) {
        return new ActionEffect(103, caster, fighter.id());
    }

    /**
     * Teleport a fighter
     *
     * @param caster The spell caster
     * @param fighter The teleport fighter
     * @param target The target cell
     */
    public static ActionEffect teleport(FighterData caster, FighterData fighter, FightCell target) {
        return new ActionEffect(4, caster, fighter.id(), target.id());
    }

    /**
     * The fighter skip the next turn
     *
     * @param caster The buff caster
     * @param fighter The target fighter
     */
    public static ActionEffect skipNextTurn(FighterData caster, FighterData fighter) {
        return new ActionEffect(140, caster, fighter.id());
    }

    /**
     * The fighter return the spell
     *
     * @param fighter The fighter
     * @param success true is the spell is successfully returned
     */
    public static ActionEffect returnSpell(FighterData fighter, boolean success) {
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
    public static ActionEffect boostSight(FighterData caster, FighterData target, int value, int duration) {
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
    public static ActionEffect decreaseSight(FighterData caster, FighterData target, int value, int duration) {
        return new ActionEffect(116, caster, target.id(), value, duration);
    }

    /**
     * Add action points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    public static ActionEffect addActionPoints(FighterData fighter, int quantity) {
        return new ActionEffect(120, fighter, fighter.id(), quantity);
    }

    /**
     * Remove action points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The AP quantity
     */
    public static ActionEffect removeActionPoints(FighterData fighter, int quantity) {
        return new ActionEffect(168, fighter, fighter.id(), -quantity);
    }

    /**
     * Add movement points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The MP quantity
     */
    public static ActionEffect addMovementPoints(FighterData fighter, int quantity) {
        return new ActionEffect(128, fighter, fighter.id(), quantity);
    }

    /**
     * Remove movement points for the current turn
     *
     * @param fighter The fighter
     * @param quantity The MP quantity
     */
    public static ActionEffect removeMovementPoints(FighterData fighter, int quantity) {
        return new ActionEffect(169, fighter, fighter.id(), -quantity);
    }

    /**
     * Suffered damage value is reduced
     *
     * @param fighter The fighter
     * @param value The reduction value
     */
    public static ActionEffect reducedDamage(FighterData fighter, int value) {
        return new ActionEffect(105, fighter, fighter.id(), value);
    }

    /**
     * Add state to the fighter
     *
     * @param fighter The fighter
     * @param state The state id to add
     */
    public static ActionEffect addState(FighterData fighter, int state) {
        return new ActionEffect(950, fighter, fighter.id(), state, 1);
    }

    /**
     * Remove state from the fighter
     *
     * @param fighter The fighter
     * @param state The state id to remove
     */
    public static ActionEffect removeState(FighterData fighter, int state) {
        return new ActionEffect(950, fighter, fighter.id(), state, 0);
    }

    /**
     * Remove all buffs that can be removed from the target
     * 
     * @param caster The caster
     * @param target The target
     */
    public static ActionEffect dispelBuffs(FighterData caster, FighterData target) {
        return new ActionEffect(132, caster, target.id());
    }

    /**
     * The fighter has been slided (i.e. move back or front) by the caster
     *
     * @param caster The spell caster
     * @param target The target (which has moved)
     * @param destination The destination cell
     */
    public static ActionEffect slide(FighterData caster, FighterData target, FightCell destination) {
        return new ActionEffect(5, caster, target.id(), destination.id());
    }

    /**
     * Damage as been reflected by the target
     *
     * @param castTarget The original cast target
     * @param value Reflected value
     */
    public static ActionEffect reflectedDamage(FighterData castTarget, int value) {
        return new ActionEffect(107, castTarget, castTarget.id(), value);
    }

    /**
     * The target has dodged the lost of action points
     *
     * @param caster The spell caster
     * @param target The target (which has dodged point lost)
     * @param value The dodged point list value
     */
    public static ActionEffect dodgeActionPointLost(FighterData caster, FighterData target, int value) {
        return new ActionEffect(308, caster, target.id(), value);
    }

    /**
     * The target has dodged the lost of movement points
     *
     * @param caster The spell caster
     * @param target The target (which has dodged point lost)
     * @param value The dodged point list value
     */
    public static ActionEffect dodgeMovementPointLost(FighterData caster, FighterData target, int value) {
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
    public static ActionEffect changeAppearance(FighterData caster, FighterData target, int newAppearance, int duration) {
        return new ActionEffect(149, caster, target.id(), target.sprite().gfxId(), newAppearance, duration);
    }

    /**
     * Reset the appearance of the target
     */
    public static ActionEffect resetAppearance(FighterData caster, FighterData target) {
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
    public static ActionEffect launchVisualEffect(FighterData caster, FightCell targetCell, Spell spell) {
        return new ActionEffect(208, caster, targetCell.id(), spell.spriteId(), spell.spriteArgs(), spell.level());
    }

    /**
     * The fighter has been make invisible
     *
     * @param caster Spell caster
     * @param target Effect target
     */
    public static ActionEffect fighterHidden(FighterData caster, FighterData target) {
        return new ActionEffect(150, caster, target.id(), 1);
    }

    /**
     * The invisibility effect is terminated
     *
     * @param caster Spell caster
     * @param target Effect target
     */
    public static ActionEffect fighterVisible(FighterData caster, FighterData target) {
        return new ActionEffect(150, caster, target.id(), 0);
    }

    /**
     * Add an invoked creature to the fight
     *
     * @param caster Invoker
     * @param invocation Invocation to add
     */
    public static ActionEffect addInvocation(FighterData caster, FighterData invocation) {
        return new ActionEffect(181, caster, "+" + invocation.sprite());
    }

    /**
     * A glyph has been triggered by a fighter
     *
     * @param caster The caster of the glyph
     * @param target The fight who triggered the glyph
     * @param glyphCell The glyph cell
     * @param spell The glyph spell
     */
    public static ActionEffect glyphTriggered(FighterData caster, FighterData target, FightCell glyphCell, Spell spell) {
        return new ActionEffect(307, target, spell.id(), glyphCell.id(), 0, spell.level(), 0, caster.id());
    }

    /**
     * A trap has been triggered by a fighter
     *
     * @param caster The caster of the trap
     * @param target The fight who triggered the trap
     * @param trapCell The trap cell
     * @param spell The trap spell
     */
    public static ActionEffect trapTriggered(FighterData caster, FighterData target, FightCell trapCell, Spell spell) {
        return new ActionEffect(
            306,
            target,
            spell.id(),
            trapCell.id(),
            spell.spriteId(),
            spell.level(),
            0, // bInFrontOfSprite ?
            caster.id()
        );
    }

    /**
     * The spell cannot be cast because of an invisible obstacle
     *
     * @param caster The spell caster
     * @param spell The spell
     */
    public static ActionEffect spellBlockedByInvisibleObstacle(FighterData caster, Spell spell) {
        return new ActionEffect(151, caster, spell.id());
    }

    /**
     * Send a custom packet, but queued on the caster sequencer
     *
     * @param caster Effect caster
     * @param packet Packet to send
     */
    public static ActionEffect packet(FighterData caster, Object packet) {
        return new ActionEffect(999, caster, packet);
    }
}
