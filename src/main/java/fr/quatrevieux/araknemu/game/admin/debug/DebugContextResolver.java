package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;

/**
 * Resolver for debug context
 */
final public class DebugContextResolver implements ContextResolver {
    final private Container container;

    public DebugContextResolver(Container container) {
        this.container = container;
    }

    @Override
    public Context resolve(Context globalContext, Object argument) throws ContextException {
        try {
            return new DebugContext(container);
        } catch (ContainerException e) {
            throw new ContextException(e);
        }
    }

    @Override
    public String type() {
        return "debug";
    }
}
