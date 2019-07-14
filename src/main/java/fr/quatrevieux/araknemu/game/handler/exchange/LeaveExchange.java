package fr.quatrevieux.araknemu.game.handler.exchange;

import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.exchange.LeaveExchangeRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Leave the current exchange or request
 */
final public class LeaveExchange implements PacketHandler<GameSession, LeaveExchangeRequest> {
    @Override
    public void handle(GameSession session, LeaveExchangeRequest packet) {
        session.exploration().interactions().get(ExchangeInteraction.class).leave();
    }

    @Override
    public Class<LeaveExchangeRequest> packet() {
        return LeaveExchangeRequest.class;
    }
}
