package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FightEndTest extends FightBaseCase {
    @Test
    void generateWithoutRewards() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        other.fighter().life().alter(player.fighter(), -1000);

        Fighter winner = player.fighter();
        Fighter looser = other.fighter();

        assertEquals(
            "GE" + fight.duration() + "|1|0|2;1;Bob;50;0;5350000;5481459;5860000;;;;;|0;2;Other;1;1;0;0;110;;;;;",
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
            ).toString()
        );
    }

    @Test
    void generateWithReward() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        other.fighter().life().alter(player.fighter(), -1000);

        Fighter winner = player.fighter();
        Fighter looser = other.fighter();

        assertEquals(
            "GE" + fight.duration() + "|1|0|2;1;Bob;50;0;5350000;5481459;5860000;1145;;;;250|0;2;Other;1;1;0;0;110;;;;;",
            new FightEnd(
                new FightRewardsSheet(
                    new EndFightResults(
                        fight,
                        Collections.singletonList(winner),
                        Collections.singletonList(looser)
                    ),
                    FightRewardsSheet.Type.NORMAL,
                    Arrays.asList(
                        new DropReward(RewardType.WINNER, winner, Collections.emptyList(), 1145, 250),
                        new DropReward(RewardType.LOOSER, looser)
                    )
                )
            ).toString()
        );
    }
}