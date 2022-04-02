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
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.ChangeMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

/**
 * Teleport to Astrub statue
 */
public final class GoToAstrub implements Action {
    private final ExplorationMapService service;

    public GoToAstrub(ExplorationMapService service) {
        this.service = service;
    }

    @Override
    public boolean check(ExplorationPlayer player) {
        return true;
    }

    @Override
    public void apply(ExplorationPlayer player) {
        final Position position = player.player().race().astrubPosition();
        final ExplorationMap targetMap = service.load(position.map());

        if (position.cell() >= targetMap.size()) {
            throw new IllegalStateException("Invalid cell for astrub map " + position);
        }

        player.interactions().push(new ChangeMap(player, targetMap, position.cell(), 7));
        player.player().setSavedPosition(position);
        player.send(Information.positionSaved());
    }

    public static final class Factory implements ActionFactory {
        private final ExplorationMapService service;

        public Factory(ExplorationMapService service) {
            this.service = service;
        }

        @Override
        public String type() {
            return "GOTO_ASTRUB";
        }

        @Override
        public Action create(ResponseAction entity) {
            return new GoToAstrub(service);
        }
    }
}
