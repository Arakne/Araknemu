package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Optional;

/**
 * Chain AI factories
 */
final public class ChainAiFactory implements AiFactory {
    final private AiFactory[] factories;

    public ChainAiFactory(AiFactory... factories) {
        this.factories = factories;
    }

    @Override
    public Optional<AI> create(Fighter fighter) {
        for (AiFactory factory : factories) {
            Optional<AI> ai = factory.create(fighter);

            if (ai.isPresent()) {
                return ai;
            }
        }

        return Optional.empty();
    }
}
