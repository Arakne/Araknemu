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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.game;

import fr.arakne.utils.encoding.Base64;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Send the start positions on fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L145
 */
public final class FightStartPositions {
    private final List<Integer>[] places;
    private final int team;

    public FightStartPositions(List<Integer>[] places, int team) {
        this.places = places;
        this.team = team;
    }

    @Override
    public String toString() {
        return "GP" +
            Arrays.stream(places)
                .map(
                    list -> list
                        .stream()
                        .map(i -> Base64.encode(i, 2))
                        .collect(Collectors.joining())
                )
                .collect(Collectors.joining("|")) +
            "|" + team
        ;

    }
}
