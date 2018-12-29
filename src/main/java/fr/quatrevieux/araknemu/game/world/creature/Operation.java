package fr.quatrevieux.araknemu.game.world.creature;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;

/**
 * Operation to apply on a creature
 */
public interface Operation {
    /**
     * Apply the operation a an exploration player
     */
    default public void onExplorationPlayer(ExplorationPlayer player) {}

    /**
     * Apply the operation on a NPC
     */
    default public void onNpc(GameNpc npc) {}
}
