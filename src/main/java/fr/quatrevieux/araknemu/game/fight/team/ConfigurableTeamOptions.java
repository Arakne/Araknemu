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

package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.team.event.AllowJoinTeamChanged;
import fr.quatrevieux.araknemu.game.fight.team.event.AllowSpectatorChanged;
import fr.quatrevieux.araknemu.game.fight.team.event.NeedHelpChanged;

/**
 * Handle fight team options toggles
 *
 * Use only on fight team with players.
 * Because monster teams are not configurable, this implementation should not be used.
 */
public final class ConfigurableTeamOptions implements TeamOptions {
    private final FightTeam team;
    private final Fight fight;

    private boolean allowSpectators = ALLOW_SPECTATOR_DEFAULT;
    private boolean allowJoinTeam = ALLOW_JOIN_DEFAULT;
    private boolean needHelp = NEED_HELP_DEFAULT;

    public ConfigurableTeamOptions(FightTeam team, Fight fight) {
        this.team = team;
        this.fight = fight;
    }

    @Override
    public boolean allowSpectators() {
        return allowSpectators;
    }

    @Override
    public boolean allowJoinTeam() {
        return allowJoinTeam;
    }

    @Override
    public boolean needHelp() {
        return needHelp;
    }

    @Override
    public FightTeam team() {
        return team;
    }

    /**
     * Toggle {@link TeamOptions#allowSpectators()} option
     * The event {@link AllowSpectatorChanged} will be dispatched on the fight
     *
     * @see fr.quatrevieux.araknemu.game.handler.fight.option.ToggleBlockSpectator
     */
    public void toggleAllowSpectators() {
        allowSpectators = !allowSpectators;
        fight.dispatch(new AllowSpectatorChanged(this, allowSpectators));
    }

    /**
     * Toggle {@link TeamOptions#allowJoinTeam()} option
     * The event {@link AllowJoinTeamChanged} will be dispatched on the fight
     *
     * Note: toggle this option on active state has no effect
     *
     * @see fr.quatrevieux.araknemu.game.handler.fight.option.ToggleLockTeam
     */
    public void toggleAllowJoinTeam() {
        allowJoinTeam = !allowJoinTeam;
        fight.dispatch(new AllowJoinTeamChanged(this, allowJoinTeam));
    }

    /**
     * Toggle {@link TeamOptions#needHelp()} option
     * The event {@link NeedHelpChanged} will be dispatched on the fight
     *
     * Note: toggle this option on active state has no effect
     *
     * @see fr.quatrevieux.araknemu.game.handler.fight.option.ToggleNeedHelp
     */
    public void toggleNeedHelp() {
        needHelp = !needHelp;
        fight.dispatch(new NeedHelpChanged(this, needHelp));
    }
}
