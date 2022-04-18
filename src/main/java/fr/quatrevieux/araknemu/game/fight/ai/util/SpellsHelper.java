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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class for perform operation on spells
 *
 * @see AIHelper#spells()
 */
public final class SpellsHelper {
    private final AIHelper helper;
    private final AI<?> ai;

    SpellsHelper(AIHelper helper, AI<?> ai) {
        this.helper = helper;
        this.ai = ai;
    }

    /**
     * Get all available spells of the current fighter
     *
     * A spell is considered as available if the current fighter as enough AP to launch it.
     * It does not check if the spell can effectively be casted (cooldown, states...)
     *
     * @return Stream of available spells
     */
    public Stream<Spell> available() {
        final int actionPoints = ai.turn().points().actionPoints();

        return StreamSupport.stream(ai.fighter().spells().spliterator(), false)
            .filter(spell -> spell.apCost() <= actionPoints)
        ;
    }

    /**
     * Check if the fighter has at least one spell available
     *
     * @return true if at least one available spell is present
     */
    public boolean hasAvailable() {
        return available().findFirst().isPresent();
    }

    /**
     * Filter spells to return only those with the given effect
     * Note: it does not filter available spells : it may return a spell with too high AP required
     *
     * @param effectId Effect ID to check
     *
     * @return Stream of spells with the given effect
     */
    public Stream<Spell> withEffect(int effectId) {
        return withEffect(spellEffect -> spellEffect.effect() == effectId);
    }

    /**
     * Filter spells to return only those with the given effect
     * Note: it does not filter available spells : it may return a spell with too high AP required
     *
     * @param filter The effect predicate
     *
     * @return Stream of spells with the given effect
     */
    public Stream<Spell> withEffect(Predicate<SpellEffect> filter) {
        return StreamSupport.stream(ai.fighter().spells().spliterator(), false)
            .filter(spell -> spell.effects().stream().anyMatch(filter))
        ;
    }

    /**
     * Helper for create or simulation cast actions
     * Note: a new instance is always returned
     *
     * @param validator Validator for simulated cast action
     */
    public SpellCaster caster(CastConstraintValidator<Spell> validator) {
        return new SpellCaster(ai, helper, validator);
    }
}
