package fr.quatrevieux.araknemu.game.exploration.exchange.player;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.PlayerExchangeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerExchangeFactoriesTest extends GameBaseCase {
    private PlayerExchangeFactories factories;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factories = new PlayerExchangeFactories();
        player = explorationPlayer();
    }

    @Test
    void createPlayerExchange() throws Exception {
        ExplorationPlayer target = makeOtherExplorationPlayer();
        target.join(player.map());

        assertInstanceOf(PlayerExchangeRequest.class, factories.create(ExchangeType.PLAYER_EXCHANGE, player, target));
    }
}
