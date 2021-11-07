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
    public default Invitation invitation(ExplorationPlayer initiator, ExplorationPlayer target) {
        return new SimpleInvitation(this, initiator, target);
    }
}
