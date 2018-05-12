package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.network.game.out.fight.FightEnd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
        assertEquals(0, fight.map().size());

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
                        new DropReward(RewardType.WINNER, winner),
                        new DropReward(RewardType.LOOSER, looser)
                    )
                )
            )
        );
    }
}
