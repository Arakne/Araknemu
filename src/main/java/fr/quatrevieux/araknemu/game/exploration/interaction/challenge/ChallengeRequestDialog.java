package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.RequestDialog;

/**
 * Base type for challenge request dialogs
 */
public interface ChallengeRequestDialog extends RequestDialog {
    /**
     * Get the challenge initiator
     */
    public ExplorationPlayer initiator();
}
