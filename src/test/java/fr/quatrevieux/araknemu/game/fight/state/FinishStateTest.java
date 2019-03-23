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
        assertEquals(0, fight.map().size());

        assertBetween(100, 140, player.inventory().kamas() - lastKamas);
        assertEquals(241, player.properties().experience().current() - lastXp);
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
        assertEquals(0, fight.map().size());

        assertEquals(player.inventory().kamas(), lastKamas);
        assertEquals(player.properties().experience().current(), lastXp);
        assertEquals(0, player.properties().life().current());
        assertEquals(player.savedPosition(), player.position());
    }
}
