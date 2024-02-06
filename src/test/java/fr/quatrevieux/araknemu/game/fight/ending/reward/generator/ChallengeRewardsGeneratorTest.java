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

package fr.quatrevieux.araknemu.game.fight.ending.reward.generator;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.ChallengeRewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChallengeRewardsGeneratorTest extends FightBaseCase {
    private Fight fight;
    private ChallengeRewardsGenerator generator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        generator = new ChallengeRewardsGenerator();
    }

    @Test
    void generate() {
        FightRewardsSheet sheet = generator.generate(
            new EndFightResults(fight, Collections.singletonList(player.fighter()), Collections.singletonList(other.fighter()))
        );

        assertSame(FightRewardsSheet.Type.NORMAL, sheet.type());
        assertSame(fight, sheet.results().fight());

        List<FightReward> rewards = new ArrayList<>(sheet.rewards());

        assertCount(2, rewards);
        assertContainsOnly(DropReward.class, rewards);

        assertSame(player.fighter(), rewards.get(0).fighter());
        assertEquals(RewardType.WINNER, rewards.get(0).type());
        assertEquals(0, DropReward.class.cast(rewards.get(0)).xp());
        assertEquals(0, DropReward.class.cast(rewards.get(0)).guildXp());
        assertEquals(0, DropReward.class.cast(rewards.get(0)).mountXp());
        assertEquals(0, DropReward.class.cast(rewards.get(0)).kamas());

        assertSame(other.fighter(), rewards.get(1).fighter());
        assertEquals(RewardType.LOOSER, rewards.get(1).type());
        assertEquals(0, DropReward.class.cast(rewards.get(1)).xp());
        assertEquals(0, DropReward.class.cast(rewards.get(1)).guildXp());
        assertEquals(0, DropReward.class.cast(rewards.get(1)).mountXp());
        assertEquals(0, DropReward.class.cast(rewards.get(1)).kamas());
    }

    @Test
    void supports() throws SQLException {
        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
        ;

        assertTrue(generator.supports(player.fighter()));
        assertTrue(generator.supports(new MonsterFighter(
            -10, container.get(MonsterService.class).load(36).get(1),
            player.fighter().team()
        )));
        assertFalse(generator.supports(new DoubleFighter(-10, player.fighter())));

        Fighter invoc = new DoubleFighter(-10, player.fighter());
        invoc.characteristics().alterDiscernment(10);

        assertFalse(generator.supports(invoc));
    }
}
