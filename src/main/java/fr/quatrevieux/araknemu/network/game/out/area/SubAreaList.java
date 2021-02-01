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

package fr.quatrevieux.araknemu.network.game.out.area;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.game.exploration.area.ExplorationSubArea;

import java.util.Collection;

/**
 * List all available subAreas
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Subareas.as#L19
 */
public final class SubAreaList {
    private final Collection<ExplorationSubArea> subAreas;

    public SubAreaList(Collection<ExplorationSubArea> subAreas) {
        this.subAreas = subAreas;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("al");

        for (ExplorationSubArea subArea : subAreas) {
            if (subArea.alignment() == Alignment.NONE) {
                continue;
            }

            sb.append('|').append(subArea.id()).append(';').append(subArea.alignment().id());
        }

        return sb.toString();
    }
}
