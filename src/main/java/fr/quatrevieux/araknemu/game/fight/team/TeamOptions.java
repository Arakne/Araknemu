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
 * Get the team options values
 */
public interface TeamOptions {
    /** Default value of {@link TeamOptions#allowSpectators()} */
    public static final boolean ALLOW_SPECTATOR_DEFAULT = true;
    /** Default value of {@link TeamOptions#allowJoinTeam()} */
    public static final boolean ALLOW_JOIN_DEFAULT = true;
    /** Default value of {@link TeamOptions#needHelp()} */
    public static final boolean NEED_HELP_DEFAULT = false;

    /**
     * Does spectators are allowed ?
     * If at least one of the team have defined this option to false, spectators should not be able to join the fight
     *
     * @return true if allowed
     */
    public boolean allowSpectators();

    /**
     * Does the {@link TeamOptions#allowSpectators()} has been changed (i.e. the value is not the default one)
     *
     * @return true if changed
     */
    public default boolean allowSpectatorHasBeenUpdated() {
        return allowSpectators() != ALLOW_SPECTATOR_DEFAULT;
    }

    /**
     * Does new fighters are allowed to join the team ?
     *
     * @return true if allowed
     */
    public boolean allowJoinTeam();

    /**
     * Does the {@link TeamOptions#allowJoinTeam()} has been changed (i.e. the value is not the default one)
     *
     * @return true if changed
     */
    public default boolean allowJoinTeamHasBeenUpdated() {
        return allowJoinTeam() != ALLOW_JOIN_DEFAULT;
    }

    /**
     * Help is needed for the team
     * An indicator should be displayed on the team "flag"
     *
     * @return true if help is needed
     */
    public boolean needHelp();

    /**
     * Does the {@link TeamOptions#needHelp()} has been changed (i.e. the value is not the default one)
     *
     * @return true if changed
     */
    public default boolean needHelpHasBeenUpdated() {
        return needHelp() != NEED_HELP_DEFAULT;
    }

    /**
     * Get the related team
     */
    public FightTeam team();
}
