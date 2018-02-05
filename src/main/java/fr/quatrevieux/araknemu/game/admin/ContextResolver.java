package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.exception.ContextException;

/**
 * Resolver for console context
 */
public interface ContextResolver {
    /**
     * Resolve the context
     *
     * @param globalContext The global context
     * @param argument The resolver argument
     */
    public Context resolve(Context globalContext, Object argument) throws ContextException;

    /**
     * Get the resolver type
     */
    public String type();
}
