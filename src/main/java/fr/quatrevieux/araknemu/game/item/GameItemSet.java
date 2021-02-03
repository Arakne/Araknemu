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

package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.Collections;
import java.util.List;

/**
 * ItemSet for game
 */
public final class GameItemSet {
    private final ItemSet entity;
    private final List<Bonus> bonus;

    public GameItemSet(ItemSet entity, List<Bonus> bonus) {
        this.entity = entity;
        this.bonus = bonus;
    }

    public int id() {
        return entity.id();
    }

    public String name() {
        return entity.name();
    }

    /**
     * Get the item set bonus for given number of items
     *
     * @param nbOfItems The number of weared items
     *                  If this number is less than 2, the bonus will always be empty
     *                  If this number is higher than maximum bonus level, the maximal bonus will be returned
     */
    public Bonus bonus(int nbOfItems) {
        if (nbOfItems <= 1) {
            return Bonus.EMPTY;
        }

        if (nbOfItems > bonus.size() + 1) {
            nbOfItems = bonus.size() + 1;
        }

        return bonus.get(nbOfItems - 2);
    }

    public static final class Bonus {
        private static final Bonus EMPTY = new Bonus(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        private final List<ItemTemplateEffectEntry> effects;
        private final List<CharacteristicEffect> characteristics;
        private final List<SpecialEffect> specials;

        public Bonus(List<ItemTemplateEffectEntry> effects, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials) {
            this.effects = effects;
            this.characteristics = characteristics;
            this.specials = specials;
        }

        public List<ItemTemplateEffectEntry> effects() {
            return effects;
        }

        public List<CharacteristicEffect> characteristics() {
            return characteristics;
        }

        public List<SpecialEffect> specials() {
            return specials;
        }
    }
}
