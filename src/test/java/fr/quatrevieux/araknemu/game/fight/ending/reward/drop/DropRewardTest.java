package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DropRewardTest extends FightBaseCase {
    @Test
    void getters() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter fighter = player.fighter();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList(), 1145, 250);

        assertSame(RewardType.WINNER, reward.type());
        assertSame(fighter, reward.fighter());

        assertEquals(1145, reward.xp());
        assertEquals(250, reward.kamas());
        assertEquals(0, reward.mountXp());
        assertEquals(0, reward.guildXp());
    }

    @Test
    void renderForWinner() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter winner = player.fighter();

        DropReward reward = new DropReward(RewardType.WINNER, winner, Collections.emptyList(), 1145, 250);

        assertEquals("2;1;Bob;50;0;5350000;5481459;5860000;1145;;;;250", reward.render());
    }

    @Test
    void renderForLooser() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter fighter = player.fighter();

        DropReward reward = new DropReward(RewardType.LOOSER, fighter, Collections.emptyList(), 0, 0);

        assertEquals("0;1;Bob;50;0;5350000;5481459;5860000;;;;;", reward.render());
    }

    @Test
    void renderForMonster() throws Exception {
        Fight fight = createPvmFight();
        fight.nextState();

        Fighter fighter = fight.team(1).fighters().stream().findFirst().get();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList(), 0, 0);

        assertEquals("2;-1;31;4;0;0;0;0;;;;;", reward.render());
    }

    @Test
    void apply() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        Fighter fighter = player.fighter();
        DropRewardAction action = Mockito.mock(DropRewardAction.class);

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.singletonList(action), 1145, 250);

        reward.apply();

        Mockito.verify(action).apply(reward, fighter);
    }
}
