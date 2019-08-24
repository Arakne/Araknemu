package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;

/**
 * Factory for a single exchange type
 *
 * @param <C> The target creature type
 */
public interface ExchangeTypeFactory<C extends ExplorationCreature> {
    /**
     * Get the supported exchange type
     */
    public ExchangeType type();

    /**
     * Creates the exchange interaction
     *
     * @param initiator The exchange initiator (current player)
     * @param target The exchange target
     */
    public ExchangeInteraction create(ExplorationPlayer initiator, C target);
}
