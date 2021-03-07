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
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

import java.util.stream.Collectors;

/**
 * Sprite for group of monsters
 * Properties of the sprite are array of monster's property, separated by comma ","
 *
 * Format :
 * [cell];[orientation];[bonus];[sprite id];[monsters ids CSV];[sprite type: -3];[monsters fgxId^size CSV];[monsters colors;accessories;]
 *
 * @see MonsterGroup
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L561
 */
public final class MonsterGroupSprite implements Sprite {
    private final MonsterGroup group;

    public MonsterGroupSprite(MonsterGroup group) {
        this.group = group;
    }

    @Override
    public int id() {
        return group.id();
    }

    @Override
    public int cell() {
        return group.cell().id();
    }

    @Override
    public Direction orientation() {
        return group.orientation();
    }

    @Override
    public Type type() {
        return Type.MONSTER_GROUP;
    }

    @Override
    public String name() {
        return group.monsters().stream()
            .map(monster -> Integer.toString(monster.id()))
            .collect(Collectors.joining(","))
        ;
    }

    @Override
    public String toString() {
        return
            cell() + ";" +
            group.orientation().ordinal() + ";" +
            ";" + // @todo Bonus value
            id() + ";" +
            name() + ";" +
            type().id() + ";" +
            group.monsters().stream()
                .map(monster -> monster.gfxId() + "^100") // @todo size
                .collect(Collectors.joining(",")) + ";" +
            group.monsters().stream()
                .map(monster -> Integer.toString(monster.level()))
                .collect(Collectors.joining(",")) + ";" +
            group.monsters().stream()
                .map(monster -> monster.colors().toHexString(",") + ";0,0,0,0;") // @todo accessories
                .collect(Collectors.joining())
        ;
    }
}
