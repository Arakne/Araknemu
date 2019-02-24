package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PvmBuilderFactoryTest  extends GameBaseCase {
    private PvmBuilderFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new PvmBuilderFactory(container.get(FighterFactory.class));
    }

    @Test
    void create() throws ContainerException {
        assertNotNull(factory.create(container.get(FightService.class)));
    }
}
