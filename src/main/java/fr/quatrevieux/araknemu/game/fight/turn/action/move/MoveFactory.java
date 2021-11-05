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

package fr.quatrevieux.araknemu.game.fight.turn.action.move;

import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.FightPathValidator;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.StopOnEnemyValidator;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.TackleValidator;

/**
 * Factory for move action
 */
public final class MoveFactory implements MoveActionFactory {
    private final FightTurn turn;

    public MoveFactory(FightTurn turn) {
        this.turn = turn;
    }

    @Override
    public Action create(String[] arguments) {
        return create(new Decoder<>(turn.fight().map()).decode(arguments[0], turn.fighter().cell()));
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public Move create(Path<FightCell> path) {
        final FightPathValidator[] validators = new FightPathValidator[] {
            new TackleValidator(),
            new StopOnEnemyValidator(),
        };

        return new Move(turn, turn.fighter(), path, validators);
    }
}
