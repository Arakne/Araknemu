package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddKamasTest extends FightBaseCase {
    private AddKamas action;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new AddKamas();
        fight = createPvmFight();
        fight.nextState();

        fighter = player.fighter();
    }

    @Test
    void applyWithoutKamas() {
        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList(), 0, 0);

        long lastKamas = player.properties().kamas();

        action.apply(reward, fighter);

        assertEquals(lastKamas, player.properties().kamas());
    }

    @Test
    void applyWithKamas() {
        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList(), 0, 1000);

        long lastKamas = player.properties().kamas();

        action.apply(reward, fighter);

        assertEquals(1000, player.properties().kamas() - lastKamas);
    }

    @Test
    void applyOnMonster() {
        MonsterFighter fighter = (MonsterFighter) fight.team(1).fighters().stream().findFirst().get();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList(), 0, 1000);

        action.apply(reward, fighter);
    }
}
