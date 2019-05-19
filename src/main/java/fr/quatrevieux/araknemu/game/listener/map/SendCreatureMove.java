package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.event.CreatureMoving;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Send to the map the sprite move
 */
final public class SendCreatureMove implements Listener<CreatureMoving> {
    final private ExplorationMap map;

    public SendCreatureMove(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(CreatureMoving event) {
        map.send(new GameActionResponse("", ActionType.MOVE, event.creature().id(), event.path().encode()));
    }

    @Override
    public Class<CreatureMoving> event() {
        return CreatureMoving.class;
    }
}
