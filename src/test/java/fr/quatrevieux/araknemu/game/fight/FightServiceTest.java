package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FightServiceTest extends GameBaseCase {
    private FightService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        service = new FightService(
            container.get(MapTemplateRepository.class),
            container.get(ListenerAggregate.class),
            Arrays.asList(
                new ChallengeBuilderFactory()
            )
        );
    }

    @Test
    void map() throws ContainerException {
        assertNotNull(service.map(container.get(ExplorationMapService.class).load(10340)));
    }

    @Test
    void handler() {
        assertNotNull(service.handler(ChallengeBuilder.class));
    }
}