package fr.quatrevieux.araknemu.game.handler.exchange;

import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.exchange.ExchangeReady;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Toggle the accept state of the exchange party
 */
final public class AcceptExchange implements PacketHandler<GameSession, ExchangeReady> {
    @Override
    public void handle(GameSession session, ExchangeReady packet) {
        session.exploration().interactions().get(ExchangeDialog.class).accept();
    }

    @Override
    public Class<ExchangeReady> packet() {
        return ExchangeReady.class;
    }
}
