package fr.quatrevieux.araknemu.game.exploration.exchange.player;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactoryAggregate;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeTypeFactory;
import fr.quatrevieux.araknemu.game.exploration.exchange.SimpleExchangeTypeFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.PlayerExchangeRequest;

/**
 * Factories for player exchanges
 */
final public class PlayerExchangeFactories extends ExchangeFactoryAggregate<ExplorationPlayer> {
    @SafeVarargs
    public PlayerExchangeFactories(ExchangeTypeFactory<ExplorationPlayer>... factories) {
        super(factories);

        register(new SimpleExchangeTypeFactory<>(ExchangeType.PLAYER_EXCHANGE, PlayerExchangeRequest::new));
    }
}
