package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.AddExperience;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

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
        long lastXp = player.properties().experience().current();

        listener.on(new FightLeaved(new DropReward(RewardType.WINNER, player.fighter(), Arrays.asList(new AddExperience()), 1000, 0)));

        assertEquals(lastXp + 1000, player.properties().experience().current());
    }

    @Test
    void withoutReward() {
        listener.on(new FightLeaved());
    }
}
