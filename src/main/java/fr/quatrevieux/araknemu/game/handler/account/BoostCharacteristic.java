package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.AskBoost;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Boost player characteristic
 */
final public class BoostCharacteristic implements PacketHandler<GameSession, AskBoost> {
    @Override
    public void handle(GameSession session, AskBoost packet) throws Exception {
        session.player()
            .characteristics()
            .boostCharacteristic(packet.characteristic())
        ;
    }

    @Override
    public Class<AskBoost> packet() {
        return AskBoost.class;
    }
}
