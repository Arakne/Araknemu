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

package fr.quatrevieux.araknemu.game.listener.map.monster;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LaunchMonsterFightTest extends GameBaseCase {
    private LaunchMonsterFight listener;
    private ExplorationPlayer player;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
            .pushMonsterGroups()
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
        ;

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(10340, 123, 1));
        map = container.get(ExplorationMapService.class).load(10340);

        listener = new LaunchMonsterFight();
        player = explorationPlayer();
        player.changeMap(map, 123);
    }

    @Test
    void onMoveFinishedWithoutMonsters() {
        listener.on(new PlayerMoveFinished(player, map.get(185)));

        assertTrue(map.creatures().contains(player));
    }

    @Test
    void onMoveFinishedWithMonsterGroupShouldStartsFight() {
        listener.on(new PlayerMoveFinished(player, map.get(123)));

        assertFalse(map.creatures().contains(player));
        assertTrue(player.player().isFighting());
        assertInstanceOf(PvmType.class, player.player().fighter().fight().type());
    }
}
