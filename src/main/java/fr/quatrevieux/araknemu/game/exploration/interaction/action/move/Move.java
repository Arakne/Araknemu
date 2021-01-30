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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.move;

import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidationException;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidator;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Move the player
 */
final public class Move implements BlockingAction {
    final private ExplorationPlayer player;
    final private PathValidator[] validators;
    private Path<ExplorationMapCell> path;

    private int id;

    public Move(ExplorationPlayer player, Path<ExplorationMapCell> path, PathValidator[] validators) {
        this.player = player;
        this.path = path;
        this.validators = validators;
    }

    @Override
    public void start(ActionQueue queue) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Empty path");
        }

        try {
            for (PathValidator validator : validators) {
                path = validator.validate(this, path);
            }
        } catch (PathValidationException e) {
            player.send(GameActionResponse.NOOP);
            e.errorPacket().ifPresent(player::send);
            return;
        }

        if (path.target().equals(player.cell())) {
            player.send(GameActionResponse.NOOP);
            return;
        }

        queue.setPending(this);
        player.map().send(new GameActionResponse(this));
    }

    @Override
    public void cancel(String argument) {
        if (argument == null) {
            return;
        }

        final int cellId = Integer.parseInt(argument);

        for (PathStep<ExplorationMapCell> step : path) {
            if (step.cell().id() == cellId) {
                player.move(step.cell(), step.direction());

                return;
            }
        }

        throw new IllegalArgumentException("Invalid cell");
    }

    @Override
    public void end() {
        player.move(path.target(), path.last().direction());
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public Object[] arguments() {
        return new Object[] { path.encodeWithStartCell() };
    }

    public Path<ExplorationMapCell> path() {
        return path;
    }
}
