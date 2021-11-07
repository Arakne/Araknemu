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

package fr.quatrevieux.araknemu.game.monster.group.generator;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.monster.Monster;

import java.util.List;

/**
 * Generate the monster list using randomized or fixed strategy depending of {@link MonsterGroupData#maxSize()} value
 */
public final class MonsterListGeneratorSwitch implements MonsterListGenerator {
    private final MonsterListGenerator randomizedGenerator;
    private final MonsterListGenerator fixedGenerator;

    /**
     * @param randomizedGenerator Generator to used when maxSize > 0
     * @param fixedGenerator Generator to used when maxSize = 0
     */
    public MonsterListGeneratorSwitch(MonsterListGenerator randomizedGenerator, MonsterListGenerator fixedGenerator) {
        this.randomizedGenerator = randomizedGenerator;
        this.fixedGenerator = fixedGenerator;
    }

    @Override
    public List<Monster> generate(MonsterGroupData data) {
        return data.maxSize() > 0
            ? randomizedGenerator.generate(data)
            : fixedGenerator.generate(data)
        ;
    }
}
