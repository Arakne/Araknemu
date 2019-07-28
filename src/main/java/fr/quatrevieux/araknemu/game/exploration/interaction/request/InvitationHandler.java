package fr.quatrevieux.araknemu.game.exploration.interaction.request;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Handle the invitation process
 */
public interface InvitationHandler {
    /**
     * Check if challenge invitation is valid
     * Error packet can be send
     */
    public boolean check(Invitation invitation);

    /**
     * Send packet to clients to open the invitation dialog
     */
    public void acknowledge(Invitation invitation);

    /**
     * Refuse or cancel the invitation
     * Send the packets, the interactions are automatically stopped
     *
     * @param invitation The invitation
     * @param dialog The dialog of the performer
     */
    public void refuse(Invitation invitation, RequestDialog dialog);

    /**
     * Accept the invitation (can only done by the target)
     * Send packets, and start the real interaction. The interactions are automatically stopped
     *
     * @param invitation The invitation
     * @param dialog The dialog acceptor (only the target)
     */
    public void accept(Invitation invitation, TargetRequestDialog dialog);

    /**
     * Creates the dialog of the initiator (i.e. who ask the invitation)
     */
    public RequestDialog initiatorDialog(Invitation invitation);

    /**
     * Creates the dialog of the target (i.e. who receive the invitation)
     */
    public TargetRequestDialog targetDialog(Invitation invitation);

    /**
     * Creates the invitation
     *
     * @param initiator The invitation initiator
     * @param target The invitation target
     */
    default public Invitation invitation(ExplorationPlayer initiator, ExplorationPlayer target) {
        return new SimpleInvitation(this, initiator, target);
    }
}
