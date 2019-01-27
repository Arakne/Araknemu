package fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Resolve value for a variable parameter
 */
public interface VariableResolver {
    /**
     * Get the variable name
     */
    public String name();

    /**
     * Resolve the variable value from the player
     */
    public Object value(ExplorationPlayer player);
}
