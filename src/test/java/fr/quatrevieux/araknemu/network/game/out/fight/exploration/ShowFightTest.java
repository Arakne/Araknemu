package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShowFightTest extends FightBaseCase {
    @Test
    void generate() throws Exception {
        dataSet.pushMaps();

        Fight fight = createFight(false);

        assertEquals("Gc+1;0|1;200;0;-1|2;210;0;-1", new ShowFight(fight).toString());
    }
}