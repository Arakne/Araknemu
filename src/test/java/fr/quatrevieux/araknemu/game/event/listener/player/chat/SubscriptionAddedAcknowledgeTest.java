package fr.quatrevieux.araknemu.game.event.listener.player.chat;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.event.common.ChannelSubscriptionAdded;
import fr.quatrevieux.araknemu.network.adapter.Channel;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelSubscribed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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
