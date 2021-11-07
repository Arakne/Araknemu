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

package fr.quatrevieux.araknemu.game.monster.group;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.group.generator.MonsterListGenerator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates monster groups
 */
public final class MonsterGroupFactory {
    private final MonsterListGenerator generator;

    /**
     * Last group id for generate id sequence
     */
    private final AtomicInteger lastGroupId = new AtomicInteger();

    public MonsterGroupFactory(MonsterListGenerator generator) {
        this.generator = generator;
    }

    /**
     * Create the monster group from data
     */
    public MonsterGroup create(MonsterGroupData data, LivingMonsterGroupPosition handler) {
        return new MonsterGroup(
            handler,
            lastGroupId.incrementAndGet(),
            generator.generate(data),
            Direction.SOUTH_EAST,
            handler.cell(),
            data.winFightTeleport()
        );
    }
}
