package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NpcExchangeFactoriesTest extends GameBaseCase {
    private NpcExchangeFactories factories;
    private ExplorationPlayer player;
    private NpcService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushNpcs()
            .pushNpcWithStore()
        ;

        service = container.get(NpcService.class);
        factories = new NpcExchangeFactories();
        player = explorationPlayer();
    }

    @Test
    void createNpcStore() {
        GameNpc npc = service.get(10001);
        ExchangeInteraction interaction = factories.create(ExchangeType.NPC_STORE, player, npc);

        assertInstanceOf(StoreDialog.class, interaction);

        player.interactions().start(interaction);
        requestStack.assertOne(new ExchangeCreated(ExchangeType.NPC_STORE, npc));
    }
}
