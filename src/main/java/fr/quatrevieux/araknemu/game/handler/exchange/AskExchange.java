package fr.quatrevieux.araknemu.game.handler.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactory;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.exchange.ExchangeRequest;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequestError;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Handle the exchange request
 */
final public class AskExchange implements PacketHandler<GameSession, ExchangeRequest> {
    final private ExchangeFactory factory;

    public AskExchange(ExchangeFactory factory) {
        this.factory = factory;
    }

    @Override
    public void handle(GameSession session, ExchangeRequest packet) throws ErrorPacket {
        final ExplorationPlayer player = session.exploration();

        try {
            if (packet.id().isPresent()) {
                player.interactions().start(factory.create(packet.type(), player, player.map().creature(packet.id().get())));
            }
        } catch (RuntimeException e) {
            throw new ErrorPacket(new ExchangeRequestError(ExchangeRequestError.Error.CANT_EXCHANGE));
        }

        // @todo handle cell
    }

    @Override
    public Class<ExchangeRequest> packet() {
        return ExchangeRequest.class;
    }
}
