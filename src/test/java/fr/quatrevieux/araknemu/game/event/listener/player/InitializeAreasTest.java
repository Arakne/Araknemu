package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.GameJoined;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.network.game.out.area.SubAreaList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import static org.junit.jupiter.api.Assertions.*;

class InitializeAreasTest extends GameBaseCase {
    private InitializeAreas listener;
    private AreaService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSubAreas();

        service = container.get(AreaService.class);
        service.preload(NOPLogger.NOP_LOGGER);

        listener = new InitializeAreas(gamePlayer(), service);
    }

    @Test
    void onGameJoined() {
        listener.on(new GameJoined());

        requestStack.assertLast(
            new SubAreaList(service.list())
        );
    }
}
