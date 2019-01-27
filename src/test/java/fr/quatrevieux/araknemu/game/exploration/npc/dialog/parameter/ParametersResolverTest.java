package fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ParametersResolverTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ParametersResolver resolver;
    private Logger logger;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        resolver = new ParametersResolver(
            new VariableResolver[] {new GetterResolver("id", ExplorationPlayer::id)},
            logger = Mockito.mock(Logger.class)
        );
    }

    @Test
    void resolveConstant() {
        assertEquals("my constant", resolver.resolve("my constant", player));
        Mockito.verify(logger, Mockito.never()).warn(Mockito.anyString());
    }

    @Test
    void resolveUndefinedVariable() {
        assertEquals("[my_var]", resolver.resolve("[my_var]", player));
        Mockito.verify(logger).warn("Undefined dialog variable {}", "my_var");
    }

    @Test
    void resolveVariable() {
        assertEquals(1, resolver.resolve("[id]", player));
    }
}
