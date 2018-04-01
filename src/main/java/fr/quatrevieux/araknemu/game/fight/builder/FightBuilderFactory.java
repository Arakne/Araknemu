package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.fight.FightService;

/**
 * Factory for fight builder
 */
public interface FightBuilderFactory<B extends FightBuilder> {
    /**
     * Get the builder class
     */
    public Class<B> type();

    /**
     * Create the builder
     */
    public B create(FightService service);
}
