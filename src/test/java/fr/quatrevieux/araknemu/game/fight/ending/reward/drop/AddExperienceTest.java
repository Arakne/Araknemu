package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.AddExperience;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AddExperienceTest extends FightBaseCase {
    private AddExperience action;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new AddExperience();
        fight = createPvmFight();
        fight.nextState();

        fighter = player.fighter();
    }

    @Test
    void applyWithoutXp() {
        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());

        long lastXp = player.properties().experience().current();

        action.apply(reward, fighter);

        assertEquals(lastXp, player.properties().experience().current());
    }

    @Test
    void applyWithXp() {
        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());
        reward.setXp(1000);

        long lastXp = player.properties().experience().current();

        action.apply(reward, fighter);

        assertEquals(1000, player.properties().experience().current() - lastXp);
    }

    @Test
    void applyOnMonster() {
        MonsterFighter fighter = (MonsterFighter) fight.team(1).fighters().stream().findFirst().get();

        DropReward reward = new DropReward(RewardType.WINNER, fighter, Collections.emptyList());
        reward.setXp(1000);

        action.apply(reward, fighter);
    }
}
