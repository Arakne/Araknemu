package fr.quatrevieux.araknemu.game.exploration.interaction.exchange;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.exchange.PlayerExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Accaptable;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequestError;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequested;

import java.util.Arrays;
import java.util.Collection;

/**
 * Request for a player exchange
 *
 * The request store the two parties
 *
 * @todo Refactor with challenge request
 */
final public class PlayerExchangeRequest implements ExchangeInteraction, Sender, Accaptable {
    final private ExplorationPlayer initiator;
    final private ExplorationPlayer target;

    public PlayerExchangeRequest(ExplorationPlayer initiator, ExplorationPlayer target) {
        this.initiator = initiator;
        this.target = target;
    }

    @Override
    public Interaction start() {
        if (!check()) {
            return null;
        }

        send(new ExchangeRequested(initiator, target, ExchangeType.PLAYER_EXCHANGE));

        target.interactions().start(new TargetExchangeRequestDialog(this));

        return new ExchangeRequestDialog(this);
    }

    @Override
    public void stop() {
        interlocutors().forEach(player -> player.interactions().remove());
    }

    @Override
    public void send(Object packet) {
        interlocutors().forEach(player -> player.send(packet));
    }

    @Override
    public void leave() {
        stop();

        send(ExchangeLeaved.cancelled());
    }

    @Override
    public void accept() {
        stop();

        for(ExchangeParty party : PlayerExchangeParty.make(initiator, target)) {
            party.start();
        }
    }

    /**
     * Get all invitation interlocutors
     */
    public Collection<ExplorationPlayer> interlocutors() {
        return Arrays.asList(initiator, target);
    }

    /**
     * Check if the exchange request is valid
     */
    private boolean check() {
        if (!initiator.restrictions().canExchange() || !target.restrictions().canExchange()) {
            return error(ExchangeRequestError.Error.CANT_EXCHANGE);
        }

        if (target.interactions().busy()) {
            if (target.interactions().get(Interaction.class) instanceof ExchangeInteraction) {
                return error(ExchangeRequestError.Error.ALREADY_EXCHANGE);
            }

            return error(ExchangeRequestError.Error.CANT_EXCHANGE);
        }

        if (!target.map().equals(initiator.map())) {
            return error(ExchangeRequestError.Error.CANT_EXCHANGE);
        }

        if (initiator.inventory().overweight()) {
            return error(ExchangeRequestError.Error.OVERWEIGHT);
        }

        return true;
    }

    /**
     * Send error
     */
    private boolean error(ExchangeRequestError.Error error) {
        initiator.send(new ExchangeRequestError(error));

        return false;
    }
}
