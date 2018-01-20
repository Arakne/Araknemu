package fr.quatrevieux.araknemu.game.event.listener.player.chat;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.event.common.ChannelSubscriptionRemoved;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelUnsubscribed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SubscriptionRemovedAcknowledgeTest extends GameBaseCase {
    private SubscriptionRemovedAcknowledge listner;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listner = new SubscriptionRemovedAcknowledge(
            gamePlayer()
        );
    }

    @Test
    void onSubscriptionRemoved() {
        listner.on(
            new ChannelSubscriptionRemoved(Arrays.asList(ChannelType.PRIVATE, ChannelType.MESSAGES))
        );

        requestStack.assertLast(
            new ChannelUnsubscribed(Arrays.asList(ChannelType.PRIVATE, ChannelType.MESSAGES))
        );
    }
}
