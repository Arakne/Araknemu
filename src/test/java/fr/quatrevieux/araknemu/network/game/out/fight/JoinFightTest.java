package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JoinFightTest extends FightBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
    }

    @Test
    void generateForChallenge() throws Exception {
        Fight fight = createFight();

        assertEquals(
            "GJK2|1|1|0||0",
            new JoinFight(fight).toString()
        );
    }
}
