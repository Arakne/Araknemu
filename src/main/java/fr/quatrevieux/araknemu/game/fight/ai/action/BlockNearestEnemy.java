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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Comparator;
import java.util.Optional;

/**
 * Try to block the nearest enemy of the invoker
 * If the invoker is hidden, this generator will act like {@link MoveNearEnemy}
 *
 * @param <F> the fighter type
 */
public final class BlockNearestEnemy<F extends ActiveFighter> implements ActionGenerator<F> {
    private final ActionGenerator<F> moveGenerator;

    @SuppressWarnings("methodref.receiver.bound")
    public BlockNearestEnemy() {
        moveGenerator = new MoveNearFighter<>(this::resolve);
    }

    @Override
    public void initialize(AI<F> ai) {
        moveGenerator.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI<F> ai, AiActionFactory actions) {
        return moveGenerator.generate(ai, actions);
    }

    private Optional<? extends FighterData> resolve(AI<F> ai) {
        final FighterData invoker = ai.fighter().invoker();

        if (invoker == null || invoker.hidden()) {
            return ai.enemy();
        }

        final CoordinateCell<BattlefieldCell> currentCell = invoker.cell().coordinate();

        return ai.helper().enemies().stream()
            .filter(fighter -> !fighter.hidden())
            .min(Comparator
                .<FighterData>comparingInt(f -> currentCell.distance(f.cell()))
                .thenComparingInt(f -> f.life().current())
            )
        ;
    }
}
