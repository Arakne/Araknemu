package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.PlayerExchangeRequest;

/**
 * Factory for exchanges
 */
final public class ExchangeFactory {
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
        // @todo Refactor when other types will be implemented
        if (type != ExchangeType.PLAYER_EXCHANGE) {
            throw new IllegalArgumentException("Unsupported type");
        }

        // @todo Use visitor when refactor
        if (!(target instanceof ExplorationPlayer)) {
            throw new IllegalArgumentException("Bad target");
        }

        return new PlayerExchangeRequest(initiator, (ExplorationPlayer) target);
    }
}
