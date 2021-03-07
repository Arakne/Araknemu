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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.util.DofusDate;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * List of fights on the current map
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Fights.as#L55
 */
public final class FightList {
    private final Collection<Fight> fights;

    public FightList(Collection<Fight> fights) {
        this.fights = fights;
    }

    @Override
    public String toString() {
        return "fL" + fights.stream()
            .map(
                fight ->
                    fight.id() + ";" +
                    (fight.active() ? DofusDate.fromDuration(fight.duration()).toMilliseconds() : -1) + ";" +
                    fight.teams().stream()
                        .map(team -> team.type() + "," + team.alignment().id() + "," + team.fighters().size())
                        .collect(Collectors.joining(";"))
            )
            .collect(Collectors.joining("|"))
        ;
    }
}
