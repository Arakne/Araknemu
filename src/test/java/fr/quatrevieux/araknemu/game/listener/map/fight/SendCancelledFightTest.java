package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.HideFight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendCancelledFightTest extends FightBaseCase {
    private Fight fight;
    private ExplorationMap map;
    private SendCancelledFight listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().join(map);

        fight = createSimpleFight(map);
        listener = new SendCancelledFight(map, container.get(FightService.class));
        requestStack.clear();
    }

    @Test
    void onFightCancelled() {
        listener.on(new FightCancelled(fight));

        requestStack.assertAll(
            new HideFight(fight),
            new FightsCount(1)
        );
    }
}
