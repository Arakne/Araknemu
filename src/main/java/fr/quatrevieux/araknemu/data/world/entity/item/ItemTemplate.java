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
 * Template for game items
 */
public final class ItemTemplate {
    private final int id;
    private final int type;
    private final String name;
    private final int level;
    private final List<ItemTemplateEffectEntry> effects;
    private final int weight;
    private final String condition;
    private final int itemSet;
    private final String weaponInfo;
    private final int price;

    public ItemTemplate(int id, int type, String name, int level, List<ItemTemplateEffectEntry> effects, int weight, String condition, int itemSet, String weaponInfo, int price) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.level = level;
        this.effects = effects;
        this.weight = weight;
        this.condition = condition;
        this.itemSet = itemSet;
        this.weaponInfo = weaponInfo;
        this.price = price;
    }

    public int id() {
        return id;
    }

    public int type() {
        return type;
    }

    public String name() {
        return name;
    }

    public int level() {
        return level;
    }

    public List<ItemTemplateEffectEntry> effects() {
        return effects;
    }

    public int weight() {
        return weight;
    }

    public String condition() {
        return condition;
    }

    public int itemSet() {
        return itemSet;
    }

    public String weaponInfo() {
        return weaponInfo;
    }

    public int price() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ItemTemplate template = (ItemTemplate) o;

        return id == template.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
