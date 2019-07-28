package fr.quatrevieux.araknemu.game.exploration.interaction.request;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 *
 */
abstract public class AbstractInitiatorRequestDialog extends AbstractRequestDialog {
    public AbstractInitiatorRequestDialog(Invitation invitation) {
        super(invitation);
    }

    @Override
    final public ExplorationPlayer self() {
        return invitation.initiator();
    }

    @Override
    final public ExplorationPlayer interlocutor() {
        return invitation.target();
    }
}
