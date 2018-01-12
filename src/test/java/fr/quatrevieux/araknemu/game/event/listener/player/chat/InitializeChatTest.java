package fr.quatrevieux.araknemu.game.event.listener.player.chat;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.event.common.GameJoined;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelSubscribed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class InitializeChatTest extends GameBaseCase {
    private InitializeChat  listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new InitializeChat(
            gamePlayer()
        );
    }

    @Test
    void onGameJoined() {
        listener.on(new GameJoined());

        requestStack.assertLast(
            new ChannelSubscribed(
                EnumSet.allOf(ChannelType.class)
            )
        );
    }
}
