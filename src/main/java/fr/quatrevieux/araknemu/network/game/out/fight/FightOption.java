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

package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.team.TeamOptions;

/**
 * Change a fight option of a team flag
 * Note: this packet must be sent during placement state
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L1191
 */
public final class FightOption {
    private final int teamId;
    private final Type option;
    private final boolean enabled;

    public FightOption(int teamId, Type option, boolean enabled) {
        this.teamId = teamId;
        this.option = option;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Go" + (enabled ? "+" : "-") + option.c + teamId;
    }

    /**
     * Create the {@link FightOption} packet for the block spectator option
     *
     * @param options The changed option
     */
    public static FightOption blockSpectators(TeamOptions options) {
        return new FightOption(options.team().id(), Type.BLOCK_SPECTATOR, !options.allowSpectators());
    }

    /**
     * Create the {@link FightOption} packet for the block joiner option
     *
     * @param options The changed option
     */
    public static FightOption blockJoiner(TeamOptions options) {
        return new FightOption(options.team().id(), Type.BLOCK_JOINER, !options.allowJoinTeam());
    }

    /**
     * Create the {@link FightOption} packet for the need help option
     *
     * @param options The changed option
     */
    public static FightOption needHelp(TeamOptions options) {
        return new FightOption(options.team().id(), Type.NEED_HELP, options.needHelp());
    }

    public static enum Type {
        NEED_HELP('H'),
        BLOCK_SPECTATOR('S'),
        BLOCK_JOINER('A'),
        BLOCK_JOINER_EXCEPT_PARTY_MEMBER('P'),
        ;

        private final char c;

        Type(char c) {
            this.c = c;
        }
    }
}
