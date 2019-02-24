package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void generateForPvm() throws Exception {
        Fight fight = createPvmFight();

        assertRegex("GJK2\\|0\\|1\\|0\\|[0-9]{5}\\|4", new JoinFight(fight).toString());
    }
}
