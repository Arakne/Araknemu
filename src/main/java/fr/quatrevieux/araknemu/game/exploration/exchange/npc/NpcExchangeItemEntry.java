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

package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Adapt an item template to inventory entry for the exchange events
 */
final class NpcExchangeItemEntry implements ItemEntry {
    private final ItemTemplate template;

    NpcExchangeItemEntry(ItemTemplate template) {
        this.template = template;
    }

    @Override
    public int id() {
        return template.id();
    }

    @Override
    public int position() {
        return DEFAULT_POSITION;
    }

    @Override
    public Item item() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int quantity() {
        return 0;
    }

    @Override
    public void add(int quantity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int quantity) throws InventoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ItemTemplateEffectEntry> effects() {
        return template.effects();
    }

    @Override
    public int templateId() {
        return template.id();
    }

    @Override
    public int hashCode() {
        return template.id();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return Objects.equals(template, ((NpcExchangeItemEntry) o).template);
    }
}
