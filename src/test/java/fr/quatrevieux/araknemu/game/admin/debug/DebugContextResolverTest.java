package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.NullContext;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebugContextResolverTest extends GameBaseCase {
    private DebugContextResolver resolver;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        resolver = new DebugContextResolver(container);
    }

    @Test
    void resolve() throws ContextException {
        assertInstanceOf(DebugContext.class, resolver.resolve(new NullContext(), null));
    }
}
