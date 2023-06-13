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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;

/**
 * Base class for implements a fighter which can play
 * Add a {@link FightTurn} handling
 */
public abstract class AbstractPlayableFighter extends AbstractFighter implements PlayableFighter {
    private @Nullable FightTurn turn;

    @Override
    public void play(FightTurn turn) {
        this.turn = turn;
    }

    @Override
    public void stop() {
        turn = null;
    }

    @Override
    public final FightTurn turn() {
        if (turn == null) {
            throw new FightException("It's not your turn");
        }

        return turn;
    }

    @Override
    public final void perform(Consumer<FightTurn> action) {
        final FightTurn turn = this.turn;

        if (turn != null) {
            action.accept(turn);
        }
    }

    @Override
    public boolean isPlaying() {
        return turn != null && turn.active();
    }
}
