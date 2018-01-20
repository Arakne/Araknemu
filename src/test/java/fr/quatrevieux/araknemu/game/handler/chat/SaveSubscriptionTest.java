package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.network.game.in.chat.SubscribeChannels;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelSubscribed;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelUnsubscribed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SaveSubscriptionTest extends GameBaseCase {
    private SaveSubscription handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        container.get(ChatService.class).preload(NOPLogger.NOP_LOGGER);
        gamePlayer(true);

        handler = new SaveSubscription();
    }

    @Test
    void handleAddSubscription() throws Exception {
        gamePlayer().subscriptions().clear();

        handler.handle(session, new SubscribeChannels(true, Arrays.asList(
            ChannelType.PRIVATE,
            ChannelType.MESSAGES
        )));

        gamePlayer().subscriptions().containsAll(
            Arrays.asList(
                ChannelType.PRIVATE,
                ChannelType.MESSAGES
            )
        );

        requestStack.assertLast(
            new ChannelSubscribed(
                Arrays.asList(
                    ChannelType.PRIVATE,
                    ChannelType.MESSAGES
                )
            )
        );
    }

    @Test
    void handleRemoveSubscriptions() throws Exception {
        handler.handle(session, new SubscribeChannels(false, Arrays.asList(
            ChannelType.PRIVATE,
            ChannelType.MESSAGES
        )));

        assertFalse(gamePlayer().subscriptions().contains(ChannelType.PRIVATE));
        assertFalse(gamePlayer().subscriptions().contains(ChannelType.MESSAGES));

        requestStack.assertLast(
            new ChannelUnsubscribed(
                Arrays.asList(
                    ChannelType.PRIVATE,
                    ChannelType.MESSAGES
                )
            )
        );
    }
}
