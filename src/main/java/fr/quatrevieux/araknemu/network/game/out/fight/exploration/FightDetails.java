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
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

import java.util.stream.Collectors;

/**
 * Details for one fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Fights.as#L86
 */
public final class FightDetails {
    private final Fight fight;

    public FightDetails(Fight fight) {
        this.fight = fight;
    }

    @Override
    public String toString() {
        return "fD" + fight.id() + "|" +
            fight.teams().stream()
                .map(FightTeam::fighters)
                .map(
                    fighters -> fighters.stream()
                        .map(fighter -> fighter.sprite().name() + "~" + fighter.level())
                        .collect(Collectors.joining(";"))
                )
                .collect(Collectors.joining("|"))
        ;
    }
}
