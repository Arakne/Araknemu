package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplyLeaveRewardTest extends FightBaseCase {
    private Fight fight;
    private ApplyLeaveReward listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new ApplyLeaveReward(player.fighter());
    }

    @Test
    void withReward() {
        long lastXp = player.experience().current();

        listener.on(new FightLeaved(new DropReward(RewardType.WINNER, player.fighter(), 1000, 0)));

        assertEquals(lastXp + 1000, player.experience().current());
    }

    @Test
    void withoutReward() {
        listener.on(new FightLeaved());
    }
}
