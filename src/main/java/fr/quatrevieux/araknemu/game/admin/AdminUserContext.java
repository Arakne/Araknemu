package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle admin users contexts
 */
final public class AdminUserContext {
    final private Map<String, Context> contexts = new HashMap<>();

    final private Context global;
    final private Context self;
    final private Map<String, ContextResolver> resolvers;

    private Context current;

    public AdminUserContext(Context global, Context self, Map<String, ContextResolver> resolvers) {
        this.global = global;
        this.self = self;
        this.current = self;
        this.resolvers = resolvers;
    }

    /**
     * Get the global context
     */
    public Context global() {
        return global;
    }

    /**
     * Get the current user context
     */
    public Context self() {
        return self;
    }

    /**
     * Get the current context
     */
    public Context current() {
        return current;
    }

    /**
     * Change the current context
     */
    public void setCurrent(Context current) {
        this.current = current;
    }

    /**
     * Get an already registered context by its name
     */
    public Context get(String name) throws ContextNotFoundException {
        if (!contexts.containsKey(name)) {
            throw new ContextNotFoundException(name);
        }

        return contexts.get(name);
    }

    /**
     * Set a context value
     */
    public void set(String name, Context context) {
        contexts.put(name, context);
    }

    /**
     * Resolve a context
     *
     * For resolve a player :
     * resolve("player", "PlayerName");
     *
     * @param type The context type
     * @param argument The context resolve argument
     */
    public Context resolve(String type, Object argument) throws ContextException {
        if (!resolvers.containsKey(type)) {
            throw new ContextException("Context type '" + type + "' not found");
        }

        return resolvers.get(type).resolve(global, argument);
    }
}
