package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;

/**
 * Base dialog for challenge invitation
 */
abstract public class ChallengeDialog implements Interaction {
    final protected ChallengeInvitation invitation;

    public ChallengeDialog(ChallengeInvitation invitation) {
        this.invitation = invitation;
    }

    @Override
    public Interaction start() {
        return this;
    }

    @Override
    public void stop() {
        decline();
    }

    /**
     * Decline the challenge invitation
     */
    public void decline() {
        invitation.cancel(this);
    }

    /**
     * Get the challenge initiator
     */
    public ExplorationPlayer initiator() {
        return invitation.initiator();
    }

    /**
     * Get the current player
     */
    abstract public ExplorationPlayer self();

    /**
     * Get the interlocutor (the other player)
     */
    abstract public ExplorationPlayer interlocutor();
}
