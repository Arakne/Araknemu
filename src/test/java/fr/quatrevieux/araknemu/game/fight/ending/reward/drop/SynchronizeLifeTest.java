package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
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
        player.fighter().life().alter(player.fighter(), -100);

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList(), 0, 0);

        action.apply(reward, player.fighter());

        assertEquals(195, player.properties().life().current());
    }

    @Test
    void applyOnPlayerFullLife() {
        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList(), 0, 0);

        action.apply(reward, player.fighter());

        assertTrue(player.properties().life().isFull());
    }

    @Test
    void applyOnMonster() {
        MonsterFighter fighter = (MonsterFighter) fight.team(1).fighters().stream().findFirst().get();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList(), 1000, 0);

        action.apply(reward, fighter);
    }
}
