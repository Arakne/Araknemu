package fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player;

import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.AbstractInitiatorRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.Invitation;

/**
 * Dialog for the exchange request
 */
final public class InitiatorExchangeRequestDialog extends AbstractInitiatorRequestDialog implements ExchangeInteraction {
    public InitiatorExchangeRequestDialog(Invitation invitation) {
        super(invitation);
    }

    @Override
    public void leave() {
        stop();
    }
}
