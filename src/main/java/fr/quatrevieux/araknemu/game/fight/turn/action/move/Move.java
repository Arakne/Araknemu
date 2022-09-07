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

import fr.arakne.utils.maps.path.Path;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.FightPathValidator;

import java.time.Duration;

/**
 * Move the fighter
 */
public final class Move implements Action {
    private final Fighter fighter;
    private final Path<FightCell> path;
    private final FightPathValidator[] validators;

    public Move(Fighter fighter, Path<FightCell> path, FightPathValidator[] validators) {
        this.fighter = fighter;
        this.path = path;
        this.validators = validators;
    }

    @Override
    public boolean validate(Turn turn) {
        return
            path.size() > 1
            && turn.points().movementPoints() >= path.size() - 1
            && path.stream().skip(1).allMatch(step -> step.cell().walkable() && step.direction().restricted())
        ;
    }

    @Override
    public ActionResult start() {
        MoveResult result = new MoveSuccess(fighter, path);

        for (FightPathValidator validator : validators) {
            result = validator.validate(this, result);

            if (!result.success()) {
                break;
            }
        }

        return result;
    }

    @Override
    public Fighter performer() {
        return fighter;
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public Duration duration() {
        // @todo handle walk and run
        return Duration.ofMillis(300L * path.size());
    }

    @Override
    public String toString() {
        return "Move{size=" + (path.size() - 1) + ", target=" + path.target().id() + '}';
    }
}
