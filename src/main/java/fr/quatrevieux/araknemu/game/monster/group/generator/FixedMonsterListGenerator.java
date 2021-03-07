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
import fr.quatrevieux.araknemu.game.monster.MonsterService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate a fixed monster group
 * The only randomization is the monster level
 * All monsters of the data will be generated
 */
public final class FixedMonsterListGenerator implements MonsterListGenerator {
    private final MonsterService service;

    public FixedMonsterListGenerator(MonsterService service) {
        this.service = service;
    }

    @Override
    public List<Monster> generate(MonsterGroupData data) {
        return data.monsters().stream()
            .map(monster -> service.load(monster.id()).random(monster.level()))
            .collect(Collectors.toList())
        ;
    }
}
