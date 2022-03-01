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

package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for equipment items
 */
public abstract class AbstractEquipment implements Item {
    private final ItemTemplate template;
    private final ItemType type;
    private final @Nullable GameItemSet set;
    private final List<CharacteristicEffect> characteristics;
    private final List<SpecialEffect> specials;

    public AbstractEquipment(ItemTemplate template, ItemType type, @Nullable GameItemSet set, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials) {
        this.template = template;
        this.type = type;
        this.set = set;
        this.characteristics = characteristics;
        this.specials = specials;
    }

    @Override
    public List<? extends ItemEffect> effects() {
        final List<ItemEffect> effects = new ArrayList<>(characteristics);

        effects.addAll(specials);

        return effects;
    }

    @Override
    public ItemTemplate template() {
        return template;
    }

    @Override
    public Optional<GameItemSet> set() {
        return Optional.ofNullable(set);
    }

    @Override
    public List<SpecialEffect> specials() {
        return specials;
    }

    @Override
    public ItemType type() {
        return type;
    }

    /**
     * Get item characteristics
     */
    public List<CharacteristicEffect> characteristics() {
        return characteristics;
    }

    /**
     * Apply equipment effect to the characteristics
     */
    public void apply(MutableCharacteristics characteristics) {
        for (CharacteristicEffect effect : this.characteristics) {
            characteristics.add(effect.characteristic(), effect.boost());
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final AbstractEquipment equipment = (AbstractEquipment) obj;

        return
            template.equals(equipment.template)
            && characteristics.equals(equipment.characteristics)
            && specials.equals(equipment.specials)
        ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, characteristics, specials);
    }
}
