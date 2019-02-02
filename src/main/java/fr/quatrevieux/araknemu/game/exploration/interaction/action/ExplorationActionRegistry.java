package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

import java.util.EnumMap;

/**
 * Registry for all exploration game actions
 */
final public class ExplorationActionRegistry implements ActionFactory {
    public interface SelfRegisterable {
        /**
         * Auto-register the action factory into the action registry
         */
        public void register(ExplorationActionRegistry factory);
    }

    final private EnumMap<ActionType, ActionFactory> factories = new EnumMap<>(ActionType.class);

    public ExplorationActionRegistry(SelfRegisterable... actions) {
        for (SelfRegisterable action : actions) {
            action.register(this);
        }
    }

    @Override
    public Action create(ExplorationPlayer player, ActionType action, String[] arguments) {
        if (!factories.containsKey(action)) {
            throw new IllegalArgumentException("No factory found for game action : " + action);
        }

        return factories.get(action).create(player, action, arguments);
    }

    /**
     * Register a new exploration action into the registry
     *
     * @param type The action type
     * @param factory The factory
     */
    public void register(ActionType type, ActionFactory factory) {
        factories.put(type, factory);
    }
}
