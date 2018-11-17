package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
class FightDetailsTest extends FightBaseCase {
    @Test
    void generate() throws Exception {
        dataSet.pushMaps();

        Fight fight = createFight(false);

        assertEquals("fD1|Bob~50|Other~1", new FightDetails(fight).toString());
    }

    @Test
    void generateWithMultipleFighterInTeam() throws Exception {
        dataSet.pushMaps();

        Fight fight = createFight();

        fight.team(0).join(makePlayerFighter(makeSimpleGamePlayer(10)));

        assertEquals("fD1|Bob~50;PLAYER_10~1|Other~1", new FightDetails(fight).toString());
    }
}