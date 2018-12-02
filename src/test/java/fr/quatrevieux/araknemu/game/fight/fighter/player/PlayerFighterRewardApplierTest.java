package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFighterRewardApplierTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighterRewardApplier applier;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        applier = new PlayerFighterRewardApplier(player.fighter());
    }

    @Test
    void dropXpReward() {
        long lastXp = player.properties().experience().current();

        applier.onDropReward(new DropReward(RewardType.WINNER, player.fighter(), 1000, 0));

        assertEquals(lastXp + 1000, player.properties().experience().current());
    }
}