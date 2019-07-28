package fr.quatrevieux.araknemu.game.handler.exchange;

import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.TargetExchangeRequestDialog;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.exchange.AcceptExchangeRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Accept the exchange request and start the exchange
 */
final public class StartExchange implements PacketHandler<GameSession, AcceptExchangeRequest> {
    @Override
    public void handle(GameSession session, AcceptExchangeRequest packet) {
        session.exploration().interactions().get(TargetExchangeRequestDialog.class).accept();
    }

    @Override
    public Class<AcceptExchangeRequest> packet() {
        return AcceptExchangeRequest.class;
    }
}
