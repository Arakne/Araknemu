package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.event.FighterAdded;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;

import java.util.Collections;

/**
 * Add the fighter to displayed fight team
 */
final public class SendTeamFighterAdded implements Listener<FighterAdded> {
    final private ExplorationMap map;

    public SendTeamFighterAdded(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(FighterAdded event) {
        map.send(new AddTeamFighters(event.fighter().team(), Collections.singleton(event.fighter())));
    }

    @Override
    public Class<FighterAdded> event() {
        return FighterAdded.class;
    }
}
