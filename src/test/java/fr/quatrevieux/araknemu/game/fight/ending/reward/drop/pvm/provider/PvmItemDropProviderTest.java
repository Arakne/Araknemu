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

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvmItemDropProviderTest extends FightBaseCase {
    private PvmItemDropProvider formula;
    private Fight fight;
    private List<MonsterFighter> monsterFighters;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        formula = new PvmItemDropProvider();
        fight = createPvmFight();

        monsterFighters = new ArrayList<>();

        for (Fighter fighter : fight.team(1).fighters()) {
            monsterFighters.add((MonsterFighter) fighter);
        }
    }

    @Test
    void withOneMonsterAndFighter() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 13, 1, 100, 100));

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());

        formula.initialize(results).provide(reward);

        Map<Integer, Integer> items = reward.items();

        assertEquals(2, items.size());
        assertEquals(1, (int) items.get(12));
        assertEquals(1, (int) items.get(13));
    }

    @Test
    void withRandomRate() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 10));

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        int count = 0;

        for (int i = 0; i < 1000; ++i) {
            DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());

            formula.initialize(results).provide(reward);
            count += reward.items().getOrDefault(12, 0);
        }

        assertBetween(90, 110, count);
    }

    @Test
    void withRandomRateAndRateMultiplier() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 10));

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        formula = new PvmItemDropProvider(2.5);

        int count = 0;

        for (int i = 0; i < 1000; ++i) {
            DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());

            formula.initialize(results).provide(reward);
            count += reward.items().getOrDefault(12, 0);
        }

        // rate = 25%
        assertBetween(225, 275, count);
    }

    @Test
    void filterByDiscernment() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 200, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 13, 1, 150, 100));

        player.properties().characteristics().specials().add(SpecialEffects.Type.DISCERNMENT, 60);

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);
        Map<Integer, Integer> items = reward.items();

        assertEquals(1, items.size());
        assertEquals(1, (int) items.get(13));
    }

    @Test
    void withMultipleFighters() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 13, 1, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 14, 4, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 15, 1, 100, 100));

        EndFightResults results = new EndFightResults(
            fight,
            Arrays.asList(player.fighter(), player.fighter(), player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        DropRewardProvider.Scope scope = formula.initialize(results);

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        scope.provide(reward);
        Map<Integer, Integer> items = reward.items();

        assertEquals(2, items.size());
        assertTrue(items.containsKey(12));
        assertTrue(items.containsKey(15));

        reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        scope.provide(reward);
        items = reward.items();

        assertEquals(2, items.size());
        assertTrue(items.containsKey(12));
        assertTrue(items.containsKey(13));

        reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        scope.provide(reward);
        items = reward.items();

        assertEquals(1, items.size());
        assertTrue(items.containsKey(14));
    }

    @Test
    void withInvocation() {
        Fighter invoc = new DoubleFighter(-10, player.fighter());
        invoc.characteristics().alterDiscernment(50);

        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 13, 1, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 14, 4, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 15, 1, 100, 100));

        EndFightResults results = new EndFightResults(
            fight,
            Arrays.asList(player.fighter(), invoc),
            Collections.singletonList(monsterFighters.get(0))
        );

        DropRewardProvider.Scope scope = formula.initialize(results);

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        scope.provide(reward);
        Map<Integer, Integer> items = reward.items();

        assertEquals(2, items.size());
        assertTrue(items.containsKey(12));
        assertTrue(items.containsKey(15));

        reward = new DropReward(RewardType.WINNER, invoc, Collections.emptyList());
        scope.provide(reward);
        items = reward.items();

        assertEquals(2, items.size());
        assertTrue(items.containsKey(12));
        assertTrue(items.containsKey(13));
    }

    @Test
    void withMultipleMonsters() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 13, 1, 100, 100));
        monsterFighters.get(1).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 100));

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Arrays.asList(monsterFighters.get(0), monsterFighters.get(1))
        );


        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);
        Map<Integer, Integer> items = reward.items();

        assertEquals(2, items.size());
        assertEquals(2, (int) items.get(12));
        assertEquals(1, (int) items.get(13));
    }
}
