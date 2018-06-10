package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.RemoveTeamFighters;

import java.util.Collections;

/**
 * Remove the fighter from displayed fight team
 */
final public class SendTeamFighterRemoved implements Listener<FighterRemoved> {
    final private ExplorationMap map;

    public SendTeamFighterRemoved(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(FighterRemoved event) {
        if (event.fight().state() instanceof PlacementState) {
            map.send(new RemoveTeamFighters(event.fighter().team(), Collections.singleton(event.fighter())));
        }
    }

    @Override
    public Class<FighterRemoved> event() {
        return FighterRemoved.class;
    }
}
