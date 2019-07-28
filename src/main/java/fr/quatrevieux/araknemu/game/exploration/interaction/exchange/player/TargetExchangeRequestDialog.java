package fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player;

import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.AbstractTargetRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.Invitation;

/**
 * Dialog for the target of the exchange request
 */
final public class TargetExchangeRequestDialog extends AbstractTargetRequestDialog implements ExchangeInteraction {
    public TargetExchangeRequestDialog(Invitation invitation) {
        super(invitation);
    }

    @Override
    public void leave() {
        stop();
    }
}
