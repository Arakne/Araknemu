package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.game.exploration.exchange.*;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;

/**
 * Factories for npc exchanges
 */
final public class NpcExchangeFactories extends ExchangeFactoryAggregate<GameNpc> {
    @SafeVarargs
    public NpcExchangeFactories(ExchangeTypeFactory<GameNpc>... factories) {
        super(factories);

        register(new SimpleExchangeTypeFactory<>(ExchangeType.NPC_STORE, (initiator, target) -> new NpcStoreExchange(initiator, target, target.store()).dialog()));
    }
}
