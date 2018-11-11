package fr.quatrevieux.araknemu.game.exploration.interaction.action;

/**
 * Factory for a single action type
 */
public interface SingleActionFactory extends ActionFactory, ExplorationActionRegistry.SelfRegisterable {
    /**
     * Get the action type to create
     */
    public ActionType type();

    @Override
    default public void register(ExplorationActionRegistry factory) {
        factory.register(type(), this);
    }
}
