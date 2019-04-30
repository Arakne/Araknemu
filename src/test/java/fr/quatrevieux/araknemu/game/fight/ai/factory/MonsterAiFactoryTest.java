package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.factory.type.Aggressive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonsterAiFactoryTest extends FightBaseCase {
    private Fight fight;
    private MonsterAiFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createPvmFight();
        factory = new MonsterAiFactory();
        factory.register("AGGRESSIVE", new Aggressive());
    }

    @Test
    void createNotAMonster() {
        assertFalse(factory.create(player.fighter()).isPresent());
    }

    @Test
    void createSuccess() {
        assertTrue(factory.create(fight.team(1).fighters().stream().findFirst().get()).isPresent());
    }
}
