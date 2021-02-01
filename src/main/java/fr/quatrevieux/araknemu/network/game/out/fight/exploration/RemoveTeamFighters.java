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

package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Remove fighters from a team flag
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L1155
 */
public final class RemoveTeamFighters {
    private final FightTeam team;
    private final Collection<? extends Fighter> fighters;

    public RemoveTeamFighters(FightTeam team, Collection<? extends Fighter> fighters) {
        this.team = team;
        this.fighters = fighters;
    }

    @Override
    public String toString() {
        return "Gt" + team.id() + "|" +
            fighters.stream()
                .map(fighter -> "-" + fighter.id())
                .collect(Collectors.joining("|"))
        ;
    }
}
