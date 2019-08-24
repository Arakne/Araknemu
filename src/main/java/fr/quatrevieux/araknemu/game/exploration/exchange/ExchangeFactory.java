package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;

import java.util.Optional;

/**
 * Factory for exchanges
 */
final public class ExchangeFactory {
    final private ExchangeFactoryAggregate<ExplorationPlayer> playerFactory;
    final private ExchangeFactoryAggregate<GameNpc> npcFactory;

    public ExchangeFactory(ExchangeFactoryAggregate<ExplorationPlayer> playerFactory, ExchangeFactoryAggregate<GameNpc> npcFactory) {
        this.playerFactory = playerFactory;
        this.npcFactory = npcFactory;
    }

    /**
     * Visitor operation for create the exchange on the valid target
     */
    final private class CreateExchange implements Operation {
        final private ExchangeType type;
        final private ExplorationPlayer initiator;

        private ExchangeInteraction exchange;

        public CreateExchange(ExchangeType type, ExplorationPlayer initiator) {
            this.type = type;
            this.initiator = initiator;
        }

        @Override
        public void onExplorationPlayer(ExplorationPlayer player) {
            exchange = playerFactory.create(type, initiator, player);
        }

        @Override
        public void onNpc(GameNpc npc) {
            exchange = npcFactory.create(type, initiator, npc);
        }

        public Optional<ExchangeInteraction> exchange() {
            return Optional.ofNullable(exchange);
        }
    }

    /**
     * Creates an exchange with a creature
     *
     * @param type The exchange type
     * @param initiator The exchange initiator (current player)
     * @param target The target
     *
     * @return The exchange interaction
     *
     * @throws IllegalArgumentException When type or target are invalid
     */
    public ExchangeInteraction create(ExchangeType type, ExplorationPlayer initiator, ExplorationCreature target) {
        CreateExchange operation = new CreateExchange(type, initiator);

        target.apply(operation);

        return operation.exchange().orElseThrow(() -> new IllegalArgumentException("Bad target"));
    }
}
