package fr.quatrevieux.araknemu.game.exploration.interaction.request;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 *
 */
abstract public class AbstractTargetRequestDialog extends AbstractRequestDialog implements TargetRequestDialog {
    public AbstractTargetRequestDialog(Invitation invitation) {
        super(invitation);
    }

    @Override
    final public void accept() {
        invitation.accept(this);
    }

    @Override
    final public ExplorationPlayer self() {
        return invitation.target();
    }

    @Override
    final public ExplorationPlayer interlocutor() {
        return invitation.initiator();
    }
}
