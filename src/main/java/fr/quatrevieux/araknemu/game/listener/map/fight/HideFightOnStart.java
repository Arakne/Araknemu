package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.HideFight;

/**
 * Hide the fight from exploration map when it starts
 */
final public class HideFightOnStart implements Listener<FightStarted> {
    final private ExplorationMap map;

    public HideFightOnStart(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(FightStarted event) {
        map.send(new HideFight(event.fight()));
    }

    @Override
    public Class<FightStarted> event() {
        return FightStarted.class;
    }
}
