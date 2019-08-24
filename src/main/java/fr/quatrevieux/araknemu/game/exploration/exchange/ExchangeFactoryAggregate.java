package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;

import java.util.EnumMap;
import java.util.Map;

/**
 * Aggregate exchange factories for a given creature type
 *
 * @param <C> The supported creature type
 */
public class ExchangeFactoryAggregate<C extends ExplorationCreature> {
    final private Map<ExchangeType, ExchangeTypeFactory<C>> factories = new EnumMap<>(ExchangeType.class);

    @SafeVarargs
    public ExchangeFactoryAggregate(ExchangeTypeFactory<C>... factories) {
        for (ExchangeTypeFactory<C> factory : factories) {
            register(factory);
        }
    }

    /**
     * Register a new factory
     */
    final protected void register(ExchangeTypeFactory<C> factory) {
        factories.put(factory.type(), factory);
    }

    /**
     * Creates the exchange interaction
     *
     * @param type The requested exchange type
     * @param initiator The exchange initiator (current player)
     * @param target The exchange target
     */
    final public ExchangeInteraction create(ExchangeType type, ExplorationPlayer initiator, C target) {
        if (!factories.containsKey(type)) {
            throw new IllegalArgumentException("Unsupported type " + type);
        }

        return factories.get(type).create(initiator, target);
    }
}
