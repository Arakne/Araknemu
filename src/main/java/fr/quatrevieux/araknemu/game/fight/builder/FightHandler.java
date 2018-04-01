package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.fight.Fight;

import java.util.function.Consumer;

/**
 * Handle fight creation
 */
final public class FightHandler<B extends FightBuilder> {
    final private B builder;

    public FightHandler(B builder) {
        this.builder = builder;
    }

    /**
     * Create and start the fight
     *
     * @param configuration The fight configuration
     */
    public Fight start(Consumer<B> configuration) {
        configuration.accept(builder);

        Fight fight = builder.build();
        fight.nextState();

        return fight;
    }
}
