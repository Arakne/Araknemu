package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;

/**
 * Simply define the factory with constructor's parameters
 *
 * @param <C> The creature type target
 */
final public class SimpleExchangeTypeFactory<C extends ExplorationCreature> implements ExchangeTypeFactory<C> {
    @FunctionalInterface
    public interface Factory<C extends ExplorationCreature> {
        public ExchangeInteraction create(ExplorationPlayer initiator, C target);
    }

    final private ExchangeType type;
    final private Factory<C> factory;

    public SimpleExchangeTypeFactory(ExchangeType type, Factory<C> factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public ExchangeType type() {
        return type;
    }

    @Override
    public ExchangeInteraction create(ExplorationPlayer initiator, C target) {
        return factory.create(initiator, target);
    }
}
