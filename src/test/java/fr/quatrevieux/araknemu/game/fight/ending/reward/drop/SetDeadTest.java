package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.SetDead;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetDeadTest extends FightBaseCase {
    private SetDead action;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new SetDead();
        fight = createPvmFight();
        fight.nextState();
    }

    @Test
    void applyOnPlayer() {
        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());

        action.apply(reward, player.fighter());

        assertEquals(0, player.properties().life().current());
    }

    @Test
    void applyOnMonster() {
        MonsterFighter fighter = (MonsterFighter) fight.team(1).fighters().stream().findFirst().get();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());

        action.apply(reward, fighter);
    }
}
