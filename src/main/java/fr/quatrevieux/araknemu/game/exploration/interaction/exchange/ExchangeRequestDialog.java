package fr.quatrevieux.araknemu.game.exploration.interaction.exchange;

import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;

/**
 * Dialog for the exchange request
 *
 * @todo refactor
 */
public class ExchangeRequestDialog implements ExchangeInteraction {
    final protected PlayerExchangeRequest request;

    public ExchangeRequestDialog(PlayerExchangeRequest request) {
        this.request = request;
    }

    @Override
    public Interaction start() {
        return this;
    }

    @Override
    public void stop() {
        request.leave();
    }

    @Override
    public void leave() {
        stop();
    }
}
