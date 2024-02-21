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

package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.SynchronizeLife;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SynchronizeLifeTest extends FightBaseCase {
    private SynchronizeLife action;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new SynchronizeLife();
        fight = createPvmFight();
        fight.nextState();
    }

    @Test
    void applyOnPlayer() {
        player.fighter().life().damage(player.fighter(), 100);

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());

        action.apply(reward, player.fighter());

        assertEquals(195, player.properties().life().current());
    }

    @Test
    void applyOnPlayerFullLife() {
        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());

        action.apply(reward, player.fighter());

        assertTrue(player.properties().life().isFull());
    }

    @Test
    void applyOnMonster() {
        MonsterFighter fighter = (MonsterFighter) fight.team(1).fighters().stream().findFirst().get();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());

        action.apply(reward, fighter);
    }
}
