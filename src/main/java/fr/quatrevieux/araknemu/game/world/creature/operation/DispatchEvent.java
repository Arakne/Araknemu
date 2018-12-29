package fr.quatrevieux.araknemu.game.world.creature.operation;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.world.creature.Operation;

/**
 * Dispatch event to the creature if supports
 */
final public class DispatchEvent implements Operation {
    final private Object event;

    public DispatchEvent(Object event) {
        this.event = event;
    }

    @Override
    public void onExplorationPlayer(ExplorationPlayer player) {
        player.dispatch(event);
    }

    @Override
    public void onNpc(GameNpc npc) {}
}
