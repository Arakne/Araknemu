package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.chat.InitializeChat;
import fr.quatrevieux.araknemu.game.event.listener.player.chat.MessageReceived;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class RegisterChatListenersTest extends GameBaseCase {
    private RegisterChatListeners listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new RegisterChatListeners();
    }

    @Test
    void onPlayerLoaded() throws SQLException, ContainerException {
        listener.on(
            new PlayerLoaded(gamePlayer())
        );

        assertTrue(gamePlayer().dispatcher().has(InitializeChat.class));
        assertTrue(gamePlayer().dispatcher().has(MessageReceived.class));
    }
}
