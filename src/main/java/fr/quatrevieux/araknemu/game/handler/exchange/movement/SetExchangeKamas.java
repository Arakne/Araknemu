package fr.quatrevieux.araknemu.game.handler.exchange.movement;

import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.exchange.movement.KamasMovement;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Set the kamas on the current exchange
 */
final public class SetExchangeKamas implements PacketHandler<GameSession, KamasMovement> {
    @Override
    public void handle(GameSession session, KamasMovement packet) {
        session.exploration().interactions().get(ExchangeDialog.class).kamas(packet.quantity());
    }

    @Override
    public Class<KamasMovement> packet() {
        return KamasMovement.class;
    }
}
