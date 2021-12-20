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

package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerFighterSpriteTest extends FightBaseCase {
    private PlayerFighterSprite sprite;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        FightMap map = container.get(FightService.class).map(
            container.get(ExplorationMapService.class).load(10340)
        );

        fighter = new PlayerFighter(gamePlayer(true));
        sprite = new PlayerFighterSprite(fighter, gamePlayer().spriteInfo());

        fighter.setTeam(new SimpleTeam(fighter, new ArrayList<>(), 0));
        fighter.move(map.get(222));
    }

    @Test
    void generate() {
        assertEquals(
            "222;1;0;1;Bob;1;10^100x100;0;50;0,0,0,0;7b;1c8;315;,,,,;295;6;3;0;0;0;0;0;0;0;0;;",
            sprite.toString()
        );
    }

    @Test
    void generateWithPointsResistance() {
        fighter.player().properties().characteristics().base().set(Characteristic.WISDOM, 100);

        assertEquals(
            "222;1;0;1;Bob;1;10^100x100;0;50;0,0,0,0;7b;1c8;315;,,,,;295;6;3;0;0;0;0;0;25;25;0;;",
            sprite.toString()
        );
    }

    @Test
    void generateDead() throws Exception {
        Fight fight = createFight(false);
        fighter.joinFight(fight, fight.map().get(222));
        fighter.init();
        fighter.life().kill(fighter);

        assertEquals(
            "-1;1;0;1;Bob;1;10^100x100;0;50;0,0,0,0;7b;1c8;315;,,,,;0;6;3;0;0;0;0;0;0;0;0;;",
            sprite.toString()
        );
    }

    @Test
    void getters() throws SQLException, ContainerException {
        assertEquals(gamePlayer().id(), sprite.id());
        assertEquals(222, sprite.cell());
        assertEquals(Sprite.Type.PLAYER, sprite.type());
        assertEquals("Bob", sprite.name());
        assertEquals(Direction.SOUTH_EAST, sprite.orientation());
    }
}
