package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExchangeFactoryAggregateTest extends GameBaseCase {
    @Test
    void createInvalidType() {
        ExchangeFactoryAggregate<ExplorationPlayer> factory = new ExchangeFactoryAggregate<>();

        assertThrows(IllegalArgumentException.class, () -> factory.create(ExchangeType.BANK, explorationPlayer(), makeOtherExplorationPlayer()));
    }

    @Test
    void createSuccess() throws Exception {
        ExplorationPlayer initiator = explorationPlayer();
        ExplorationPlayer target = makeOtherExplorationPlayer();
        ExchangeInteraction exchange = Mockito.mock(ExchangeInteraction.class);

        SimpleExchangeTypeFactory<ExplorationPlayer> typeFactory = new SimpleExchangeTypeFactory<>(
            ExchangeType.PLAYER_EXCHANGE,
            (initiator1, target1) -> exchange
        );

        ExchangeFactoryAggregate<ExplorationPlayer> factory = new ExchangeFactoryAggregate<>(typeFactory);

        assertSame(exchange, factory.create(ExchangeType.PLAYER_EXCHANGE, initiator, target));
    }
}
