package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Accaptable;

/**
 * Dialog for challenge target
 */
final public class ChallengerDialog extends ChallengeDialog implements Accaptable {
    public ChallengerDialog(ChallengeInvitation invitation) {
        super(invitation);
    }

    @Override
    public ExplorationPlayer self() {
        return invitation.challenger();
    }

    @Override
    public ExplorationPlayer interlocutor() {
        return invitation.initiator();
    }

    @Override
    public void accept() {
        invitation.accept(this);
    }
}
