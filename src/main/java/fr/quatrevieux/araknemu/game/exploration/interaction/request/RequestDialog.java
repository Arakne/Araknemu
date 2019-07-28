package fr.quatrevieux.araknemu.game.exploration.interaction.request;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Declinable;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;

/**
 * An interaction request dialog (ask dialog)
 */
public interface RequestDialog extends Interaction, Declinable {
    /**
     * Get the current player
     */
    public ExplorationPlayer self();

    /**
     * Get the interlocutor (the other player)
     */
    public ExplorationPlayer interlocutor();
}
