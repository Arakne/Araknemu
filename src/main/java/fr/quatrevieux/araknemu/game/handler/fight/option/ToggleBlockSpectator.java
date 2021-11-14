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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.fight.option;

import fr.quatrevieux.araknemu.game.fight.spectator.Spectators;
import fr.quatrevieux.araknemu.game.fight.team.ConfigurableTeamOptions;
import fr.quatrevieux.araknemu.network.game.in.fight.option.BlockSpectatorRequest;

/**
 * Toggle blocking spectators to join the fight
 * Current spectators should be kicked when changed
 *
 * Unlike other team option toggle, the block spectator is available even on active state.
 * Moreover, the "allowed" state is global to the fight and not only related to the team :
 * to allow spectators both teams must allow spectators. If at least one team block them, spectators will be blocked.
 *
 * @see Spectators#canJoin()
 * @see ConfigurableTeamOptions#allowSpectators()
 * @see ConfigurableTeamOptions#toggleAllowSpectators()
 */
public final class ToggleBlockSpectator extends AbstractToggleTeamOption<BlockSpectatorRequest> {
    @Override
    public Class<BlockSpectatorRequest> packet() {
        return BlockSpectatorRequest.class;
    }

    @Override
    protected void toggleOption(ConfigurableTeamOptions options) {
        options.toggleAllowSpectators();
    }
}
