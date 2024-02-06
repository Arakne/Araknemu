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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PvmKamasProviderTest extends FightBaseCase {
    private PvmKamasProvider formula;
    private Fight fight;
    private List<Fighter> monsterFighters;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        formula = new PvmKamasProvider();
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

        assertBetween(50, 70, reward.kamas());

        DropReward otherReward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(otherReward);

        assertNotEquals(reward.kamas(), otherReward.kamas());
    }

    @Test
    void withMultipleMonsters() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            monsterFighters
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertBetween(100, 140, reward.kamas());

        DropReward otherReward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(otherReward);

        assertNotEquals(reward.kamas(), otherReward.kamas());
    }

    @Test
    void withInvocation() {
        Fighter invoc = new DoubleFighter(-10, player.fighter());

        EndFightResults results = new EndFightResults(
            fight,
            Arrays.asList(player.fighter(), invoc),
            monsterFighters
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertBetween(100, 140, reward.kamas());

        DropReward otherReward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(otherReward);

        assertNotEquals(reward.kamas(), otherReward.kamas());

        DropReward invocReward = new DropReward(RewardType.WINNER, invoc, Collections.emptyList());
        formula.initialize(results).provide(invocReward);

        assertBetween(100, 140, invocReward.kamas());
    }

    @Test
    void withoutMonsters() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(player.fighter())
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertEquals(0, reward.kamas());
    }
}
