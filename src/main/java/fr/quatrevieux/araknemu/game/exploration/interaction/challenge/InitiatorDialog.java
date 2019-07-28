package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.AbstractInitiatorRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.Invitation;

/**
 * Dialog for challenge initiator
 */
final public class InitiatorDialog extends AbstractInitiatorRequestDialog implements ChallengeRequestDialog {
    public InitiatorDialog(Invitation invitation) {
        super(invitation);
    }

    @Override
    public ExplorationPlayer initiator() {
        return invitation.initiator();
    }
}
