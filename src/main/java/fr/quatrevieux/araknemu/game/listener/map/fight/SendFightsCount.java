package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;

/**
 * Update the fights count when fight is stopped
 */
final public class SendFightsCount implements Listener<FightStopped> {
    final private ExplorationMap map;
    final private FightService fightService;

    public SendFightsCount(ExplorationMap map, FightService fightService) {
        this.map = map;
        this.fightService = fightService;
    }

    @Override
    public void on(FightStopped event) {
        map.send(new FightsCount(fightService.fightsByMap(map.id()).size()));
    }

    @Override
    public Class<FightStopped> event() {
        return FightStopped.class;
    }
}
