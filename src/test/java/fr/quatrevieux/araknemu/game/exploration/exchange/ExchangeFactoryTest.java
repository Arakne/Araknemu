package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcExchangeFactories;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.PlayerExchangeRequest;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeFactoryTest extends GameBaseCase {
    private ExchangeFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExchangeFactory(
            new PlayerExchangeFactories(),
            new NpcExchangeFactories()
        );
    }

    @Test
    void createSuccess() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        ExchangeInteraction interaction = factory.create(ExchangeType.PLAYER_EXCHANGE, player, other);

        assertInstanceOf(PlayerExchangeRequest.class, interaction);
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

    @Test
    void createNpcStore() throws SQLException {
        dataSet.pushNpcWithStore();

        ExplorationPlayer player = explorationPlayer();
        player.join(container.get(ExplorationMapService.class).load(10340));
        ExplorationCreature target = explorationPlayer().map().creature(-1000104);

        assertInstanceOf(StoreDialog.class, factory.create(ExchangeType.NPC_STORE, player, target));
    }
}
