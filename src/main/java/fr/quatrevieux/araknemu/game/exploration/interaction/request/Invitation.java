package fr.quatrevieux.araknemu.game.exploration.interaction.request;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.world.util.Sender;

/**
 * An interaction invitation
 *
 * When started, the invitation will open the "cancel" dialog for the requester
 * and the "accept / decline" dialog for the target
 */
public interface Invitation extends Interaction, Sender {
    /**
     * Cancel / refuse the invitation
     *
     * @param dialog The refuse dialog
     */
    public void cancel(RequestDialog dialog);

    /**
     * Accept the invitation and start the fight
     * Can only be performed by the target of the request
     *
     * @param dialog The accepter dialog
     */
    public void accept(TargetRequestDialog dialog);

    /**
     * Get the initiator (i.e. who ask the invitation)
     */
    public ExplorationPlayer initiator();

    /**
     * Get the invitation target
     */
    public ExplorationPlayer target();
}
