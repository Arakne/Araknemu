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
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
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
    private final AI ai;

    private final SpellCaster caster;

    SpellsHelper(AIHelper helper, AI ai) {
        this.helper = helper;
        this.ai = ai;

        this.caster = new SpellCaster(ai);
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
     * Perform simulation of all spells through all available cells of the map
     * All returned {@link CastSimulation} can effectively be performed
     *
     * <pre> {@code
     * final SpellsHelper helper = ai.helper().spells();
     *
     * Optional<Action> action = helper.simulate(simulator)
     *     .filter(simulation -> simulation.enemiesLife() < -100) // Filter spell which cause at least 100 damage on enemies
     *     .min(Comparator.comparingInt(CastSimulation::enemiesLife)) // Get the most powerful cast action
     *     .map(helper::createAction) // Create the action from the simulation
     * ;
     * }</pre>
     *
     * @param simulator The simulator to use
     *
     * @return Stream of performed simulations
     */
    public Stream<CastSimulation> simulate(Simulator simulator) {
        final SpellCaster caster = caster();
        final ActiveFighter fighter = ai.fighter();

        return available().flatMap(spell -> helper.cells().stream()
            .filter(target -> caster.validate(spell, target)) // Validate spell (LoS, cooldown, target type...)
            .map(target -> simulator.simulate(spell, fighter, target)) // Simulate cast
        );
    }

    /**
     * Create an action from a cast simulation
     *
     * @param simulation The simulation
     *
     * @return The cast action
     */
    public Action createAction(CastSimulation simulation) {
        return caster().create(simulation.spell(), simulation.target());
    }

    /**
     * @return The {@link SpellCaster} instance
     */
    public SpellCaster caster() {
        return caster;
    }
}
