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

import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.ConfigurableTeamOptions;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.option.NeedHelpRequest;

/**
 * Toggle the need help icon on the team flag
 *
 * The toggle can only be performed on placement state (i.e. not active fight)
 *
 * @see ConfigurableTeamOptions#needHelp()
 * @see ConfigurableTeamOptions#toggleNeedHelp()
 */
public final class ToggleNeedHelp extends AbstractToggleTeamOption<NeedHelpRequest> {
    @Override
    public Class<NeedHelpRequest> packet() {
        return NeedHelpRequest.class;
    }

    @Override
    protected boolean check(GameSession session, PlayerFighter fighter) {
        return !fighter.fight().active();
    }

    @Override
    protected void toggleOption(ConfigurableTeamOptions options) {
        options.toggleNeedHelp();
    }
}
