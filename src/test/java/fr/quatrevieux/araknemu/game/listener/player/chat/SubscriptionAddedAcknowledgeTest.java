package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.event.ChannelSubscriptionAdded;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelSubscribed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SubscriptionAddedAcknowledgeTest extends GameBaseCase {
    private SubscriptionAddedAcknowledge listner;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listner = new SubscriptionAddedAcknowledge(
            gamePlayer()
        );
    }

    @Test
    void onSubscriptionAdded() {
        listner.on(
            new ChannelSubscriptionAdded(Arrays.asList(ChannelType.PRIVATE, ChannelType.MESSAGES))
        );

        requestStack.assertLast(
            new ChannelSubscribed(Arrays.asList(ChannelType.PRIVATE, ChannelType.MESSAGES))
        );
    }
}
