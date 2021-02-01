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

package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.Invitation;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.InvitationHandler;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.RequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.request.TargetRequestDialog;
import fr.quatrevieux.araknemu.game.fight.FightHandler;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Handler for challenge (duel fight) invitation dialog
 */
public final class ChallengeInvitationHandler implements InvitationHandler {
    private final FightHandler<ChallengeBuilder> fightHandler;

    public ChallengeInvitationHandler(FightHandler<ChallengeBuilder> fightHandler) {
        this.fightHandler = fightHandler;
    }

    @Override
    public boolean check(Invitation invitation) {
        if (invitation.initiator().interactions().busy()) {
            return error(invitation, JoinFightError.CANT_YOU_R_BUSY);
        }

        if (invitation.target().interactions().busy()) {
            return error(invitation, JoinFightError.CANT_YOU_OPPONENT_BUSY);
        }

        if (invitation.initiator().map() != invitation.target().map()) {
            return error(invitation, JoinFightError.CANT_BECAUSE_MAP);
        }

        if (!invitation.initiator().map().canLaunchFight()) {
            return error(invitation, JoinFightError.CANT_BECAUSE_MAP);
        }

        if (!invitation.initiator().player().restrictions().canChallenge() || !invitation.target().restrictions().canChallenge()) {
            return error(invitation, JoinFightError.CANT_FIGHT_NO_RIGHTS);
        }

        return true;
    }

    @Override
    public void acknowledge(Invitation invitation) {
        invitation.initiator().map().send(
            new GameActionResponse("", ActionType.CHALLENGE, invitation.initiator().id(), invitation.target().id())
        );
    }

    @Override
    public void refuse(Invitation invitation, RequestDialog dialog) {
        invitation.send(
            new GameActionResponse(
                "",
                ActionType.REFUSE_CHALLENGE,
                dialog.self().id(),
                dialog.interlocutor().id()
            )
        );
    }

    @Override
    public void accept(Invitation invitation, TargetRequestDialog dialog) {
        invitation.send(
            new GameActionResponse(
                "",
                ActionType.ACCEPT_CHALLENGE,
                dialog.self().id(),
                dialog.interlocutor().id()
            )
        );

        fightHandler.start(
            builder -> builder
                .map(invitation.initiator().map())
                .fighter(invitation.initiator().player())
                .fighter(invitation.target().player())
        );
    }

    @Override
    public RequestDialog initiatorDialog(Invitation invitation) {
        return new InitiatorDialog(invitation);
    }

    @Override
    public TargetRequestDialog targetDialog(Invitation invitation) {
        return new ChallengerDialog(invitation);
    }

    private boolean error(Invitation invitation, JoinFightError error) {
        invitation.initiator().send(
            new GameActionResponse(
                "",
                ActionType.JOIN_FIGHT,
                invitation.initiator().id(),
                Character.toString(error.error())
            )
        );

        return false;
    }
}
