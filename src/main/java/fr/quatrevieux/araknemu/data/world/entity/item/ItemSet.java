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

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

import java.util.List;

/**
 * Entity for item sets
 */
public final class ItemSet {
    private final int id;
    private final String name;
    private final List<List<ItemTemplateEffectEntry>> bonus;

    public ItemSet(int id, String name, List<List<ItemTemplateEffectEntry>> bonus) {
        this.id = id;
        this.name = name;
        this.bonus = bonus;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public List<List<ItemTemplateEffectEntry>> bonus() {
        return bonus;
    }
}
