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

package fr.quatrevieux.araknemu.game.fight.ai;

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base class to handle AI actions
 *
 * The AI can be seen as a pipeline of action generators :
 * - If a generator cannot generate an action, the next one is called
 * - If all generators fail, the turn is stopped
 * - When an action is successfully generated, it will be executed,
 *   and the "pipeline" is reset after the action termination
 *
 * Note: The AI execution is deferred, each action is executed by the fight executor,
 *       and the next action is scheduled after the last one.
 *       So the AI execution is not blocking, and executed in parallel of the turn timer.
 */
final public class FighterAI implements Runnable, AI {
    final private ActiveFighter fighter;
    final private Fight fight;
    final private ActionGenerator[] actions;

    private Turn turn;

    /**
     * Creates the AI
     *
     * @param fighter The fighter to control
     * @param actions The actions pipeline
     */
    public FighterAI(ActiveFighter fighter, Fight fight, ActionGenerator[] actions) {
        this.fighter = fighter;
        this.fight = fight;
        this.actions = actions;
    }

    @Override
    public void start(Turn turn) {
        this.turn = turn;

        for (ActionGenerator generator : actions) {
            generator.initialize(this);
        }

        fight.execute(this);
    }

    @Override
    public void run() {
        if (turn == null) {
            throw new IllegalStateException("AI#start() must be called before run()");
        }

        final Turn currentTurn = turn;

        for (ActionGenerator generator : actions) {
            if (!currentTurn.active()) {
                turn = null;
                return;
            }

            final Optional<Action> action = generator.generate(this);

            if (action.isPresent()) {
                currentTurn.perform(action.get());
                currentTurn.later(() -> fight.schedule(this, Duration.ofMillis(800)));
                return;
            }
        }

        turn = null;
        currentTurn.stop();
    }

    @Override
    public ActiveFighter fighter() {
        return fighter;
    }

    @Override
    public BattlefieldMap map() {
        return fight.map();
    }

    @Override
    public Turn turn() {
        return turn;
    }

    @Override
    public Stream<? extends PassiveFighter> fighters() {
        return fight.fighters().stream().filter(other -> !other.dead());
    }

    @Override
    public Stream<? extends PassiveFighter> enemies() {
        return fighters().filter(other -> !other.team().equals(fighter.team()));
    }

    @Override
    public Optional<? extends PassiveFighter> enemy() {
        final CoordinateCell<FightCell> currentCell = new CoordinateCell<>(fighter.cell());

        return enemies()
            .min(Comparator.comparingInt(f -> currentCell.distance(new CoordinateCell<>(f.cell()))))
        ;
    }
}
