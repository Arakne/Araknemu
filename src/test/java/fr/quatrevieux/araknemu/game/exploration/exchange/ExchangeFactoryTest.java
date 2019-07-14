package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.PlayerExchangeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeFactoryTest extends GameBaseCase {
    private ExchangeFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExchangeFactory();
    }

    @Test
    void createSuccess() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        ExchangeInteraction interaction = factory.create(ExchangeType.PLAYER_EXCHANGE, player, other);

        assertInstanceOf(PlayerExchangeRequest.class, interaction);
        assertCollectionEquals(((PlayerExchangeRequest) interaction).interlocutors(), player, other);
    }

    @Test
    void createInvalidType() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        assertThrows(IllegalArgumentException.class, () -> factory.create(ExchangeType.UNKNOWN_14, player, other));
    }

    @Test
    void createInvalidTarget() throws Exception {
        ExplorationPlayer player = explorationPlayer();

        assertThrows(IllegalArgumentException.class, () -> factory.create(ExchangeType.PLAYER_EXCHANGE, player, Mockito.mock(ExplorationCreature.class)));
    }
}
