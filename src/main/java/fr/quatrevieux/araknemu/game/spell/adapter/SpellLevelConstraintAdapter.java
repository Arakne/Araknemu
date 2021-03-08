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

package fr.quatrevieux.araknemu.game.spell.adapter;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;

/**
 * Adapter {@link SpellConstraints} from {@link fr.quatrevieux.araknemu.data.world.entity.SpellTemplate.Level}
 */
public final class SpellLevelConstraintAdapter implements SpellConstraints {
    private final SpellTemplate.Level level;

    public SpellLevelConstraintAdapter(SpellTemplate.Level level) {
        this.level = level;
    }

    @Override
    public Interval range() {
        return level.range();
    }

    @Override
    public boolean lineLaunch() {
        return level.lineLaunch();
    }

    @Override
    public boolean lineOfSight() {
        return level.lineOfSight();
    }

    @Override
    public boolean freeCell() {
        return level.freeCell();
    }

    @Override
    public int launchPerTurn() {
        return level.launchPerTurn();
    }

    @Override
    public int launchPerTarget() {
        return level.launchPerTarget();
    }

    @Override
    public int launchDelay() {
        return level.launchDelay();
    }

    @Override
    public int[] requiredStates() {
        return level.requiredStates();
    }

    @Override
    public int[] forbiddenStates() {
        return level.forbiddenStates();
    }
}
