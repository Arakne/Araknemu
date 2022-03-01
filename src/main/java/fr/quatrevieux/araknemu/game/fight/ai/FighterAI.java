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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base class to handle AI actions
 *
 * Note: The AI execution is deferred, each action is executed by the fight executor,
 *       and the next action is scheduled after the last one.
 *       So the AI execution is not blocking, and executed in parallel of the turn timer.
 */
public final class FighterAI implements Runnable, AI {
    private final ActiveFighter fighter;
    private final Fight fight;
    private final ActionGenerator generator;
    private final AIHelper helper;

    private @Nullable Turn turn;

    /**
     * Creates the AI
     *
     * @param fighter The fighter to control
     * @param generator The action generator
     */
    @SuppressWarnings({"argument", "assignment"})
    public FighterAI(ActiveFighter fighter, Fight fight, ActionGenerator generator) {
        this.fighter = fighter;
        this.fight = fight;
        this.generator = generator;
        this.helper = new AIHelper(this);
    }

    @Override
    public void start(Turn turn) {
        this.turn = turn;

        generator.initialize(this);
        fight.execute(this);
    }

    @Override
    public void run() {
        if (turn == null) {
            throw new IllegalStateException("AI#start() must be called before run()");
        }

        final Turn currentTurn = turn;

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
    public @NonNull Turn turn() {
        final Turn turn = this.turn;

        if (turn == null) {
            throw new IllegalStateException("AI must be started");
        }

        return turn;
    }

    @Override
    public Stream<? extends PassiveFighter> fighters() {
        return fight.fighters().stream().filter(other -> !other.dead());
    }

    @Override
    public Optional<? extends PassiveFighter> enemy() {
        return helper.enemies().nearest();
    }

    @Override
    public AIHelper helper() {
        return helper;
    }
}
