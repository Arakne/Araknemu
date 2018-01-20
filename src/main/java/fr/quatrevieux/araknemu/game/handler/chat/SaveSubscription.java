package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.SubscribeChannels;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Save chat channel subscriptions
 */
final public class SaveSubscription implements PacketHandler<GameSession, SubscribeChannels> {
    @Override
    public void handle(GameSession session, SubscribeChannels packet) throws Exception {
        if (packet.isSubscribe()) {
            session.player().subscriptions().addAll(packet.channels());
        } else {
            session.player().subscriptions().removeAll(packet.channels());
        }
    }

    @Override
    public Class<SubscribeChannels> packet() {
        return SubscribeChannels.class;
    }
}
