package fr.quatrevieux.araknemu.game.handler.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactory;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.TargetExchangeRequestDialog;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.exchange.ExchangeRequest;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskExchangeTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    private AskExchange handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.join(player.map());

        handler = new AskExchange(container.get(ExchangeFactory.class));
    }

    @Test
    void success() throws ErrorPacket {
        handler.handle(session, new ExchangeRequest(ExchangeType.PLAYER_EXCHANGE, other.id(), null));

        requestStack.assertLast(new ExchangeRequested(player, other, ExchangeType.PLAYER_EXCHANGE));

        assertInstanceOf(ExchangeRequestDialog.class, player.interactions().get(Interaction.class));
        assertInstanceOf(TargetExchangeRequestDialog.class, other.interactions().get(Interaction.class));
    }

    @Test
    void invalidTarget() {
        assertThrows(ErrorPacket.class, () -> handler.handle(session, new ExchangeRequest(ExchangeType.PLAYER_EXCHANGE, -8, null)));
    }

    @Test
    void invalidType() {
        assertThrows(ErrorPacket.class, () -> handler.handle(session, new ExchangeRequest(ExchangeType.BANK, other.id(), null)));
    }

    @Test
    void notExploring() {
        session.setExploration(null);

        assertThrows(CloseImmediately.class, () -> handlePacket(new ExchangeRequest(ExchangeType.PLAYER_EXCHANGE, other.id(), null)));
    }

    @Test
    void functional() throws Exception {
        handlePacket(new ExchangeRequest(ExchangeType.PLAYER_EXCHANGE, other.id(), null));

        assertInstanceOf(ExchangeRequestDialog.class, player.interactions().get(Interaction.class));
        assertInstanceOf(TargetExchangeRequestDialog.class, other.interactions().get(Interaction.class));
    }
}
