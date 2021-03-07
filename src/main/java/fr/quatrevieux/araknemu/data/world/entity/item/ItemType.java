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

package fr.quatrevieux.araknemu.data.world.entity.item;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.item.SuperType;

/**
 * Type for items
 */
public final class ItemType {
    private final int id;
    private final String name;
    private final SuperType superType;
    private final EffectArea effectArea;

    public ItemType(int id, String name, SuperType superType, EffectArea effectArea) {
        this.id = id;
        this.name = name;
        this.superType = superType;
        this.effectArea = effectArea;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public SuperType superType() {
        return superType;
    }

    public EffectArea effectArea() {
        return effectArea;
    }

    @Override
    public String toString() {
        return name;
    }
}
