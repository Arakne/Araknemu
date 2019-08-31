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
