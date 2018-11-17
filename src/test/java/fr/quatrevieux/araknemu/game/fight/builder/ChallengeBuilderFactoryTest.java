package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeBuilderFactoryTest extends GameBaseCase {
    private ChallengeBuilderFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ChallengeBuilderFactory(container.get(FighterFactory.class));
    }

    @Test
    void create() throws ContainerException {
        assertNotNull(factory.create(container.get(FightService.class)));
    }
}
