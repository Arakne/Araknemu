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

package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PvmXpProviderTest extends FightBaseCase {
    private PvmXpProvider formula;
    private Fight fight;
    private List<Fighter> monsterFighters;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        formula = new PvmXpProvider();
        fight = createPvmFight();

        monsterFighters = new ArrayList<>(fight.team(1).fighters());
    }

    @Test
    void withOneMonster() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertEquals(25, reward.xp());
    }

    @Test
    void withoutMonster() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.emptyList()
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertEquals(0, reward.xp());
    }

    @Test
    void withTwoMonsters() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            monsterFighters
        );


        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertEquals(241, reward.xp());
    }

    @Test
    void rate() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            monsterFighters
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        new PvmXpProvider(2.5).initialize(results).provide(reward);

        assertEquals(604, reward.xp());
    }

    @Test
    void withMultipleWinners() {
        assertEquals(20, xpForMultipleWinners(2));
        assertEquals(21, xpForMultipleWinners(3));
        assertEquals(33, xpForMultipleWinners(4));
        assertEquals(36, xpForMultipleWinners(5));
        assertEquals(39, xpForMultipleWinners(6));
        assertEquals(43, xpForMultipleWinners(7));
        assertEquals(47, xpForMultipleWinners(8));
    }

    @Test
    void withMultipleMonsters() {
        assertEquals(55, xpForMultipleMonsters(2));
        assertEquals(89, xpForMultipleMonsters(3));
        assertEquals(124, xpForMultipleMonsters(4));
        assertEquals(156, xpForMultipleMonsters(5));
        assertEquals(187, xpForMultipleMonsters(6));
        assertEquals(218, xpForMultipleMonsters(7));
        assertEquals(249, xpForMultipleMonsters(8));
        assertEquals(249, xpForMultipleMonsters(8));
    }

    @Test
    void wisdomBonus() {
        player.properties().characteristics().base().add(Characteristic.WISDOM, 100);

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertEquals(51, reward.xp());
    }

    private long xpForMultipleWinners(int nbWinners) {
        List<Fighter> winners = new ArrayList<>(nbWinners);

        for (; nbWinners > 0; --nbWinners) {
            winners.add(player.fighter());
        }

        EndFightResults results = new EndFightResults(
            fight,
            winners,
            Collections.singletonList(monsterFighters.get(0))
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        return reward.xp();
    }

    private long xpForMultipleMonsters(int nbMonsters) {
        List<Fighter> monsters = new ArrayList<>(nbMonsters);

        for (; nbMonsters > 0; --nbMonsters) {
            monsters.add(monsterFighters.get(0));
        }

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            monsters
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        return reward.xp();
    }
}
