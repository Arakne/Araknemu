package fr.quatrevieux.araknemu.game.exploration.interaction.exchange;

import fr.quatrevieux.araknemu.game.exploration.interaction.Accaptable;

/**
 * Dialog for the target of the exchange request
 *
 * @todo refactor
 */
public class TargetExchangeRequestDialog extends ExchangeRequestDialog implements Accaptable {
    public TargetExchangeRequestDialog(PlayerExchangeRequest request) {
        super(request);
    }

    @Override
    public void accept() {
        request.accept();
    }
}
