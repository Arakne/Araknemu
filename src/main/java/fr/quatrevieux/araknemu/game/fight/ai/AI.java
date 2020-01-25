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

package fr.quatrevieux.araknemu.game.fight.ai;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;

import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base class for handle AI actions
 *
 * The AI can be seen as a pipeline of action generators :
 * - If a generator cannot generates an action, the next one is called
 * - If all generators failed, the turn is stopped
 * - When an action is successfully generated, it will be executed,
 *   and the "pipeline" is reset after the action termination
 *
 * Note: The AI execution is deferred, each action is executed by the fight executor,
 *       and the next action is scheduled after the last one.
 *       So the AI execution is not blocking, and executed in parallel of the turn timer.
 */
final public class AI implements Runnable {
    final private Fighter fighter;
    final private ActionGenerator[] actions;

    private FightTurn turn;

    /**
     * Creates the AI
     *
     * @param fighter The fighter to control
     * @param actions The actions pipeline
     */
    public AI(Fighter fighter, ActionGenerator[] actions) {
        this.fighter = fighter;
        this.actions = actions;
    }

    /**
     * Start the AI
     * The AI will be pushed into the fight to be executed
     *
     * @param turn The current turn
     */
    public void start(FightTurn turn) {
        this.turn = turn;

        for (ActionGenerator generator : actions) {
            generator.initialize(this);
        }

        fight().execute(this);
    }

    @Override
    public void run() {
        if (turn == null) {
            throw new IllegalStateException("AI#start() must be called before run()");
        }

        final FightTurn currentTurn = turn;

        for (ActionGenerator generator : actions) {
            if (!currentTurn.active()) {
                turn = null;
                return;
            }

            Optional<Action> action = generator.generate(this);

            if (action.isPresent()) {
                currentTurn.perform(action.get());
                currentTurn.later(() -> fight().schedule(this, Duration.ofMillis(800)));
                return;
            }
        }

        turn = null;
        currentTurn.stop();
    }

    /**
     * Get the fighter controlled by the AI
     */
    public Fighter fighter() {
        return fighter;
    }

    /**
     * Get the current fight
     */
    public Fight fight() {
        return fighter.fight();
    }

    /**
     * Get the current turn
     */
    public FightTurn turn() {
        return turn;
    }

    /**
     * Get all alive enemies of the fighter
     * This method behavior can change, depending of the AI resolution strategy
     */
    public Stream<Fighter> enemies() {
        return fight().fighters().stream()
            .filter(other -> !other.dead())
            .filter(other -> !other.team().equals(fighter.team()))
        ;
    }

    /**
     * Get the best enemy
     * This method behavior can change, depending of the AI resolution strategy
     *
     * An empty optional can be returned, if there is no enemy which match
     */
    public Optional<Fighter> enemy() {
        final CoordinateCell<FightCell> currentCell = new CoordinateCell<>(fighter.cell());

        return enemies()
            .min(Comparator.comparingInt(f -> currentCell.distance(new CoordinateCell<>(f.cell()))))
        ;
    }
}
