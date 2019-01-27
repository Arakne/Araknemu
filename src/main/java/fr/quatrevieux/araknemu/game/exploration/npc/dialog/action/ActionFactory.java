package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;

/**
 * Factory for a single action type
 */
public interface ActionFactory {
    /**
     * Get the supported action type id
     *
     * @see ResponseAction#action()
     */
    public String type();

    /**
     * Create the action from the entity
     */
    public Action create(ResponseAction entity);
}
