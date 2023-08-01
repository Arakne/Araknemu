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
import fr.quatrevieux.araknemu.game.fight.ai.action.FightAiActionFactoryAdapter;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.fight.turn.action.FightAction;
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
    private final PlayableFighter fighter;
    private final Fight fight;
    private final ActionGenerator generator;
    private final AIHelper helper;

    private @Nullable FightTurn turn;

    /**
     * Creates the AI
     *
     * @param fighter The fighter to control
     * @param generator The action generator
     */
    @SuppressWarnings({"argument", "assignment"})
    public FighterAI(PlayableFighter fighter, Fight fight, ActionGenerator generator) {
        this.fighter = fighter;
        this.fight = fight;
        this.generator = generator;
        this.helper = new AIHelper(this);
    }

    /**
     * Start the AI
     * The AI will be pushed into the fight to be executed
     *
     * @param turn The current turn
     */
    public void start(FightTurn turn) {
        this.turn = turn;

        generator.initialize(this);
        fight.execute(this);
    }

    @Override
    public void run() {
        if (turn == null) {
            throw new IllegalStateException("AI#start() must be called before run()");
        }

        final Turn<FightAction> currentTurn = turn;

        if (!currentTurn.active()) {
            turn = null;
            return;
        }

        final Optional<FightAction> action = generator.generate(
            this,
            new FightAiActionFactoryAdapter(
                fighter,
                fight,
                fight.actions()
            )
        );

        if (action.isPresent()) {
            currentTurn.perform(action.get());
            currentTurn.later(() -> fight.schedule(this, Duration.ofMillis(800)));
            return;
        }

        turn = null;
        currentTurn.stop();
    }

    @Override
    public PlayableFighter fighter() {
        return fighter;
    }

    @Override
    public BattlefieldMap map() {
        return fight.map();
    }

    @Override
    public @NonNull FightTurn turn() {
        final FightTurn turn = this.turn;

        if (turn == null) {
            throw new IllegalStateException("AI must be started");
        }

        return turn;
    }

    @Override
    public Stream<? extends FighterData> fighters() {
        return fight.fighters().alive();
    }

    @Override
    public Optional<? extends FighterData> enemy() {
        return helper.enemies().nearest();
    }

    @Override
    public AIHelper helper() {
        return helper;
    }
}
