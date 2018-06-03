package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));

        fight.team(0).join(new PlayerFighter(makeSimpleGamePlayer(10)));

        assertEquals("fD1|PLAYER_6~1;PLAYER_10~1|PLAYER_7~1", new FightDetails(fight).toString());
    }
}