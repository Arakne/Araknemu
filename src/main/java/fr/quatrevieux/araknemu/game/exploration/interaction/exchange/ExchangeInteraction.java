package fr.quatrevieux.araknemu.game.exploration.interaction.exchange;

import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;

/**
 * Base type for exchange interactions
 */
public interface ExchangeInteraction extends Interaction {
    /**
     * Leave or cancel the exchange
     */
    public void leave();
}
