package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @todo multiple fighters
 */
class FightDetailsTest extends FightBaseCase {
    @Test
    void generate() throws Exception {
        dataSet.pushMaps();

        Fight fight = createFight(false);

        assertEquals("fD1|Bob~50|Other~1", new FightDetails(fight).toString());
    }
}