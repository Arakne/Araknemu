package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FighterPositionsTest extends GameBaseCase {
    private List<Fighter> fighters;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fighters = Arrays.asList(
            new PlayerFighter(gamePlayer(true)),
            new PlayerFighter(makeOtherPlayer())
        );

        FightMap map = container.get(FightService.class).map(
            container.get(ExplorationMapService.class).load(10340)
        );

        fighters.get(0).move(map.get(123));
        fighters.get(1).move(map.get(321));
    }

    @Test
    void generate() {
        assertEquals(
            "GIC|" + fighters.get(0).id() + ";123|" + fighters.get(1).id() + ";321",
            new FighterPositions(fighters).toString()
        );
    }
}
