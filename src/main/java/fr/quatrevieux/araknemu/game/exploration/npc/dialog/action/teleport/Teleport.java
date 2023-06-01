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

package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.teleport;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Teleportation;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.TeleportationTarget;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import fr.quatrevieux.araknemu.util.Splitter;

/**
 * Teleport to the given position
 *
 * Arguments : [mapid],[cellid],[cinematic]
 *
 * Cinematic is not required (if not set, or set to 0, no cinematic will be displayed)
 */
public final class Teleport implements Action {
    private final ExplorationMapService service;
    private final Position position;
    private final int cinematic;

    public Teleport(ExplorationMapService service, Position position, int cinematic) {
        this.service = service;
        this.position = position;
        this.cinematic = cinematic;
    }

    @Override
    public boolean check(ExplorationPlayer player) {
        // @todo check if map exists ?
        return true;
    }

    @Override
    public void apply(ExplorationPlayer player) {
        player.interactions().push(new Teleportation(
            player,
            new TeleportationTarget(
                service.load(position.map()),
                position.cell()
            ),
            cinematic
        ));
    }

    public static final class Factory implements ActionFactory {
        private final ExplorationMapService service;

        public Factory(ExplorationMapService service) {
            this.service = service;
        }

        @Override
        public String type() {
            return "TELEPORT";
        }

        @Override
        public Action create(ResponseAction entity) {
            final Splitter splitter = new Splitter(entity.arguments(), ',');

            return new Teleport(
                service,
                new Position(
                    splitter.nextNonNegativeInt(),
                    splitter.nextNonNegativeInt()
                ),
                splitter.nextIntOrDefault(0)
            );
        }
    }
}
