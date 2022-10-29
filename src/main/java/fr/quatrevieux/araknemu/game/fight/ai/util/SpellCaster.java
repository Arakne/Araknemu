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

package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.stream.Stream;

/**
 * Utility class for cast spells
 */
public final class SpellCaster {
    private final AI<?> ai;
    private final AIHelper helper;
    private final CastConstraintValidator<Spell> validator;

    public SpellCaster(AI<?> ai, AIHelper helper, CastConstraintValidator<Spell> validator) {
        this.ai = ai;
        this.helper = helper;
        this.validator = validator;
    }

    /**
     * Validate the cast action
     */
    public boolean validate(Spell spell, BattlefieldCell target) {
        // A non-walkable cell can't be a valid target
        if (!target.walkableIgnoreFighter()) {
            return false;
        }

        final Turn turn = ai.turn();

        return validator.check(turn, spell, target);
    }

    /**
     * Perform simulation of all spells through all available cells of the map
     * All returned {@link CastSimulation} can effectively be performed
     *
     * <pre> {@code
     * final SpellCaster helper = ai.helper().spells().caster(actions.cast().validator());
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
        final ActiveFighter fighter = ai.fighter();

        return helper.spells().available().flatMap(spell -> helper.cells().stream()
            .filter(target -> validate(spell, target)) // Validate spell (LoS, cooldown, target type...)
            .map(target -> simulator.simulate(spell, fighter, target)) // Simulate cast
        );
    }
}
