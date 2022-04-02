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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Type for items
 */
public final class ItemType {
    private final int id;
    private final String name;
    private final SuperType superType;
    private final @Nullable EffectArea effectArea;

    public ItemType(int id, String name, SuperType superType, @Nullable EffectArea effectArea) {
        this.id = id;
        this.name = name;
        this.superType = superType;
        this.effectArea = effectArea;
    }

    /**
     * The type id
     *
     * This is the primary key.
     * The value can be found in `items_xx_xxx.swf` file, as key of `I.t` object.
     */
    @Pure
    public int id() {
        return id;
    }

    /**
     * Human-readable item type
     * Not used internally
     */
    @Pure
    public String name() {
        return name;
    }

    @Pure
    public SuperType superType() {
        return superType;
    }

    /**
     * The area of the weapon effect
     *
     * This value is provided only for weapon types.
     * Other item types should return null.
     */
    @Pure
    public @Nullable EffectArea effectArea() {
        return effectArea;
    }

    @Override
    public String toString() {
        return name;
    }
}
