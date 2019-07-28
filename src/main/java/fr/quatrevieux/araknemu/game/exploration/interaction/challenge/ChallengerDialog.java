package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.AbstractTargetRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.Invitation;

/**
 * Dialog for challenge target
 */
final public class ChallengerDialog extends AbstractTargetRequestDialog implements ChallengeRequestDialog {
    public ChallengerDialog(Invitation invitation) {
        super(invitation);
    }

    @Override
    public ExplorationPlayer initiator() {
        return invitation.initiator();
    }
}
