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

/**
 * Immutable team options with only default values
 * Should be used for monster teams
 *
 * Note: {@link DefaultTeamOptions#allowJoinTeam()} even if is always true, a player cannot join a monster team
 */
public final class DefaultTeamOptions implements TeamOptions {
    private final FightTeam team;

    public DefaultTeamOptions(FightTeam team) {
        this.team = team;
    }

    @Override
    public boolean allowSpectators() {
        return ALLOW_SPECTATOR_DEFAULT;
    }

    @Override
    public boolean allowJoinTeam() {
        return ALLOW_JOIN_DEFAULT;
    }

    @Override
    public boolean needHelp() {
        return NEED_HELP_DEFAULT;
    }

    @Override
    public boolean allowSpectatorHasBeenUpdated() {
        return false;
    }

    @Override
    public boolean allowJoinTeamHasBeenUpdated() {
        return false;
    }

    @Override
    public boolean needHelpHasBeenUpdated() {
        return false;
    }

    @Override
    public FightTeam team() {
        return team;
    }
}
