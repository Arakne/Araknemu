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

import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;

/**
 * Base dialog for request invitation
 */
public abstract class AbstractRequestDialog implements RequestDialog {
    protected final Invitation invitation;

    public AbstractRequestDialog(Invitation invitation) {
        this.invitation = invitation;
    }

    @Override
    public final Interaction start() {
        return this;
    }

    @Override
    public final void stop() {
        decline();
    }

    /**
     * Decline the invitation
     */
    @Override
    public final void decline() {
        invitation.cancel(this);
    }
}
