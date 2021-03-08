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

import fr.arakne.utils.maps.path.Decoder;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.SingleActionFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidator;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

/**
 * Create the exploration move action
 */
public final class MoveFactory implements SingleActionFactory {
    private final PathValidator[] validators;

    public MoveFactory(PathValidator... validators) {
        this.validators = validators;
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public Action create(ExplorationPlayer player, ActionType action, String[] arguments) {
        final ExplorationMap map = player.map();

        return new Move(
            player,
            new Decoder<>(map).decode(arguments[0], map.get(player.position().cell())),
            validators
        );
    }
}
