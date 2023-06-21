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

package fr.quatrevieux.araknemu.game.listener.map;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.map.event.CreatureMoving;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SendCreatureMoveTest extends GameBaseCase {
    private SendCreatureMove listener;
    private ExplorationPlayer creature;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        creature = explorationPlayer();
        listener = new SendCreatureMove(creature.map());
    }

    @Test
    void onMove() {
        Path<ExplorationMapCell> path = new Path<>(
            new Decoder<>(creature.map()),
            Arrays.asList(
                new PathStep<>(creature.map().get(123), Direction.EAST),
                new PathStep<>(creature.map().get(138), Direction.SOUTH_EAST)
            )
        );

        listener.on(new CreatureMoving(creature, path));

        requestStack.assertLast(new GameActionResponse("", ActionType.MOVE, creature.id(), "ab7bck"));
    }
}
