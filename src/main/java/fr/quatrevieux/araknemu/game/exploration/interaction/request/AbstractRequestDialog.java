package fr.quatrevieux.araknemu.game.exploration.interaction.request;

import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;

/**
 * Base dialog for request invitation
 */
abstract public class AbstractRequestDialog implements RequestDialog {
    final protected Invitation invitation;

    public AbstractRequestDialog(Invitation invitation) {
        this.invitation = invitation;
    }

    @Override
    final public Interaction start() {
        return this;
    }

    @Override
    final public void stop() {
        decline();
    }

    /**
     * Decline the invitation
     */
    @Override
    final public void decline() {
        invitation.cancel(this);
    }
}
