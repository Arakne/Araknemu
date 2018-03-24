package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Dialog for challenge target
 */
final public class ChallengerDialog extends ChallengeDialog {
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

    public void accept() {
        invitation.accept(this);
    }
}
