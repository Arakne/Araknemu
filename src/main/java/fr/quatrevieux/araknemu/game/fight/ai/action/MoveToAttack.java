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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.util.SpellCaster;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.Optional;

/**
 * Try to move for perform an attack
 *
 * The nearest cell for perform an attack is selected.
 * If the current cell permit to attack, the fighter will not perform any move.
 *
 * For select the cell, the generator will iterates over all reachable cells
 * with the current amount of MPs, sort them by distance,
 * and check all spells on all available cells.
 * The first matching cell is selected.
 *
 * Note: This selected cell is not the best cell for perform an attack, but the nearest cell.
 *       So, it do not perform the best move for maximize damage.
 */
final public class MoveToAttack implements ActionGenerator {
    final private Movement movement;
    final private Simulator simulator;

    private SpellCaster caster;
    private Fighter fighter;
    private CoordinateCell<FightCell> currentCell;
    private AI ai;

    public MoveToAttack(Simulator simulator) {
        this.simulator = simulator;
        this.movement = new Movement(
            coordinates -> coordinates.distance(currentCell),
            scoredCell -> canAttackFromCell(scoredCell.coordinates().cell())
        );
    }

    @Override
    public void initialize(AI ai) {
        movement.initialize(ai);

        this.fighter = ai.fighter();
        this.caster = new SpellCaster(ai);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        this.ai = ai;
        final int movementPoints = ai.turn().points().movementPoints();

        // Cannot move or attack
        if (movementPoints < 1 || ai.turn().points().actionPoints() < 1) {
            return Optional.empty();
        }

        // No needs move : can attack from the current cell
        if (canAttackFromCell(fighter.cell())) {
            return Optional.empty();
        }

        currentCell = new CoordinateCell<>(fighter.cell());

        return movement.generate(ai);
    }

    /**
     * Simulate possible attacks from the given cell
     */
    private boolean canAttackFromCell(FightCell cell) {
        final FightCell lastCell = fighter.cell();
        final int actionPoints = ai.turn().points().actionPoints();

        try {
            // @todo refactor cast validation system
            fighter.move(cell);

            for (Spell spell : fighter.spells()) {
                if (spell.apCost() > actionPoints) {
                    continue;
                }

                for (FightCell targetCell : ai.fight().map()) {
                    // Target or launch is not valid
                    if (!targetCell.walkableIgnoreFighter() || !caster.validate(spell, targetCell)) {
                        continue;
                    }

                    // Simulate spell effects
                    CastSimulation simulation = simulator.simulate(spell, fighter, targetCell);

                    // The spell cause damage
                    if (simulation.enemiesLife() < 0) {
                        return true;
                    }
                }
            }
        } finally {
            // @todo refactor cast validation system
            fighter.move(lastCell);
        }

        return false;
    }
}
