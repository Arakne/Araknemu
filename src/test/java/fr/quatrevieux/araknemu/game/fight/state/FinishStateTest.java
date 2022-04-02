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

package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.network.game.out.fight.FightEnd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FinishStateTest extends FightBaseCase {
    private Fight fight;
    private FinishState state;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        state = new FinishState();
    }

    @Test
    void start() {
        other.fighter().life().alter(player.fighter(), -1000);

        Fighter winner = player.fighter();
        Fighter looser = other.fighter();

        state.start(fight);

        assertFalse(player.isFighting());
        assertFalse(other.isFighting());
        assertCount(0, fight.teams());

        requestStack.assertLast(
            new FightEnd(
                new FightRewardsSheet(
                    new EndFightResults(
                        fight,
                        Collections.singletonList(winner),
                        Collections.singletonList(looser)
                    ),
                    FightRewardsSheet.Type.NORMAL,
                    Arrays.asList(
                        new DropReward(RewardType.WINNER, winner, Collections.emptyList()),
                        new DropReward(RewardType.LOOSER, looser, Collections.emptyList())
                    )
                )
            )
        );
    }

    @Test
    void startOnWinningPvmFight() throws Exception {
        fight = createPvmFight();
        fight.nextState();

        Collection<Fighter> monsters = fight.team(1).fighters();

        long lastXp = player.properties().experience().current();
        long lastKamas = player.inventory().kamas();

        monsters.forEach(fighter -> fighter.life().kill(fighter));

        state.start(fight);

        assertFalse(player.isFighting());
        assertCount(0, fight.teams());

        assertBetween(100, 140, player.inventory().kamas() - lastKamas);
        assertEquals(241, player.properties().experience().current() - lastXp);
    }

    @Test
    void levelUpShouldRestoreLife() throws Exception {
        // Issue #192 : The life is not restored because SynchronizeLife is performed after LevelUp
        // See: https://github.com/Arakne/Araknemu/issues/192
        fight = createPvmFight();
        fight.nextState();

        player.properties().experience().add(
            player.properties().experience().max() - player.properties().experience().current() - 1
        );

        player.fighter().life().alter(player.fighter(), -100);
        Collection<Fighter> monsters = fight.team(1).fighters();

        monsters.forEach(fighter -> fighter.life().kill(fighter));

        state.start(fight);

        assertTrue(player.properties().life().isFull());
    }

    @Test
    void startOnLoosingPvmFight() throws Exception {
        fight = createPvmFight();
        fight.nextState();


        long lastXp = player.properties().experience().current();
        long lastKamas = player.inventory().kamas();

        player.fighter().life().kill(player.fighter());

        state.start(fight);

        assertFalse(player.isFighting());
        assertCount(0, fight.teams());

        assertEquals(player.inventory().kamas(), lastKamas);
        assertEquals(player.properties().experience().current(), lastXp);
        assertEquals(0, player.properties().life().current());
        assertEquals(player.savedPosition(), player.position());
    }
}
