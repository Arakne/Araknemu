package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.RegisterChatListeners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest extends GameBaseCase {
    private ChatService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ChatService(
            container.get(ListenerAggregate.class)
        );
    }

    @Test
    void preload() throws ContainerException {
        service.preload(NOPLogger.NOP_LOGGER);

        assertTrue(container.get(ListenerAggregate.class).has(RegisterChatListeners.class));
    }
}
