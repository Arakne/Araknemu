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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Wrap casting arguments
 */
public interface CastScope<F extends FighterData> {
    /**
     * Get the casted action
     */
    @Pure
    public Castable action();

    /**
     * Get the spell, if and only if the action is a spell
     */
    public Optional<Spell> spell();

    /**
     * Get the caster
     */
    @Pure
    public F caster();

    /**
     * Get the targeted cell
     */
    @Pure
    public FightCell target();

    /**
     * Get the cast targets
     *
     * This method will not resolve target mapping, nor effect target mapping
     * It will return all targets, before the mapping is resolved
     * So if {@link CastScope#replaceTarget(FighterData, FighterData)} is called,
     * the new target will be added on this set
     *
     * Note: a new instance is returned to ensure that concurrent modification will not occur
     */
    public Set<F> targets();

    /**
     * Replace a target of the cast
     *
     * @param originalTarget The base target fighter
     * @param newTarget The new target fighter
     *
     * @todo mutable scope ?
     */
    public void replaceTarget(F originalTarget, F newTarget);

    /**
     * Remove a target of the cast
     *
     * Note: this method will definitively remove the target,
     * even if {@link CastScope#replaceTarget(FighterData, FighterData)} is called
     *
     * @todo mutable scope ?
     */
    public void removeTarget(F target);

    /**
     * Get list of effects to apply
     */
    @Pure
    public List<? extends EffectScope<F>> effects();

    public static interface EffectScope<F extends FighterData> {
        /**
         * The related effect
         */
        @Pure
        public SpellEffect effect();

        /**
         * Get all targeted fighters for the current effect
         */
        public Collection<F> targets();
    }
}
