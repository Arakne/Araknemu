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

package fr.quatrevieux.araknemu.game.listener.fight;

import fr.arakne.utils.maps.MapCell;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class SendFightJoinedTest extends FightBaseCase {
    private SendFightJoined listener;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        fight = createFight();

        requestStack.clear();
        fighter = player.fighter();
        listener = new SendFightJoined(fighter);
    }

    @Test
    void onFightJoined() {
        listener.on(new FightJoined(fight, fighter));

        requestStack.assertAll(
            new JoinFight(fight),
            new AddSprites(
                Arrays.asList(
                    fighter.sprite(),
                    new ArrayList<>(fight.team(1).fighters()).get(0).sprite()
                )
            ),
            new FightStartPositions(
                new MapCell[][]{
                    fight.team(0).startPlaces().toArray(new MapCell[0]),
                    fight.team(1).startPlaces().toArray(new MapCell[0]),
                },
                0
            )
        );
    }
}
