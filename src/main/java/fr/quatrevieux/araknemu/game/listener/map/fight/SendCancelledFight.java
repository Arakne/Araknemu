package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.HideFight;

/**
 * Send to the map the cancelled fight
 */
final public class SendCancelledFight implements Listener<FightCancelled> {
    final private ExplorationMap map;
    final private FightService fightService;

    public SendCancelledFight(ExplorationMap map, FightService fightService) {
        this.map = map;
        this.fightService = fightService;
    }

    @Override
    public void on(FightCancelled event) {
        map.send(new HideFight(event.fight()));
        map.send(new FightsCount(fightService.fightsByMap(map.id()).size()));
    }

    @Override
    public Class<FightCancelled> event() {
        return FightCancelled.class;
    }
}
