/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.Invitation;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.InvitationHandler;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.RequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.TargetRequestDialog;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequestError;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequested;

/**
 * Request for a player exchange
 *
 * The request store the two parties
 */
final public class PlayerExchangeRequest implements ExchangeInteraction, InvitationHandler {
    final private Invitation invitation;

    public PlayerExchangeRequest(ExplorationPlayer initiator, ExplorationPlayer target) {
        invitation = invitation(initiator, target);
    }

    @Override
    public Interaction start() {
        return invitation.start();
    }

    @Override
    public void stop() {
        invitation.stop();
    }

    @Override
    public void leave() {
        invitation.cancel(initiatorDialog(invitation));
    }

    @Override
    public void acknowledge(Invitation invitation) {
        invitation.send(new ExchangeRequested(invitation.initiator(), invitation.target(), ExchangeType.PLAYER_EXCHANGE));
    }

    @Override
    public void refuse(Invitation invitation, RequestDialog dialog) {
        invitation.send(ExchangeLeaved.cancelled());
    }

    @Override
    public void accept(Invitation invitation, TargetRequestDialog dialog) {
        for (PlayerExchangeParty party : PlayerExchangeParty.make(invitation.initiator(), invitation.target())) {
            party.start();
        }
    }

    @Override
    public RequestDialog initiatorDialog(Invitation invitation) {
        return new InitiatorExchangeRequestDialog(invitation);
    }

    @Override
    public TargetRequestDialog targetDialog(Invitation invitation) {
        return new TargetExchangeRequestDialog(invitation);
    }

    @Override
    public boolean check(Invitation invitation) {
        if (!invitation.initiator().restrictions().canExchange() || !invitation.target().restrictions().canExchange()) {
            return error(invitation, ExchangeRequestError.Error.CANT_EXCHANGE);
        }

        if (invitation.target().interactions().busy()) {
            if (invitation.target().interactions().get(Interaction.class) instanceof ExchangeInteraction) {
                return error(invitation, ExchangeRequestError.Error.ALREADY_EXCHANGE);
            }

            return error(invitation, ExchangeRequestError.Error.CANT_EXCHANGE);
        }

        if (!invitation.target().map().equals(invitation.initiator().map())) {
            return error(invitation, ExchangeRequestError.Error.CANT_EXCHANGE);
        }

        if (invitation.initiator().inventory().overweight()) {
            return error(invitation, ExchangeRequestError.Error.OVERWEIGHT);
        }

        return true;
    }

    /**
     * Send error
     */
    private boolean error(Invitation invitation, ExchangeRequestError.Error error) {
        invitation.initiator().send(new ExchangeRequestError(error));

        return false;
    }
}
