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
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Usable item
 */
final public class UsableItem implements Item {
    final private ItemTemplate template;
    final private ItemType type;
    final private List<UseEffect> useEffects;
    final private List<SpecialEffect> specials;

    public UsableItem(ItemTemplate template, ItemType type, List<UseEffect> useEffects, List<SpecialEffect> specials) {
        this.template = template;
        this.type = type;
        this.useEffects = useEffects;
        this.specials = specials;
    }

    @Override
    public ItemTemplate template() {
        return template;
    }

    @Override
    public Optional<GameItemSet> set() {
        return Optional.empty();
    }

    @Override
    public List<? extends ItemEffect> effects() {
        final List<ItemEffect> effects = new ArrayList<>(useEffects);

        effects.addAll(specials);

        return effects;
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
     * Check if the player can use the item
     */
    public boolean check(ExplorationPlayer player) {
        for (UseEffect effect : useEffects) {
            if (!effect.check(player)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Apply the item use effects to the player
     */
    public void apply(ExplorationPlayer player) {
        for (UseEffect effect : useEffects) {
            effect.apply(player);
        }
    }

    /**
     * Check if the player can use the item to the target
     */
    public boolean checkTarget(ExplorationPlayer player, ExplorationPlayer target, int cell) {
        for (UseEffect effect : useEffects) {
            if (!effect.checkTarget(player, target, cell)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Apply the item use effects to the target
     */
    public void applyToTarget(ExplorationPlayer player, ExplorationPlayer target, int cell) {
        for (UseEffect effect : useEffects) {
            effect.applyToTarget(player, target, cell);
        }
    }

    /**
     * Check if the fighter can use the item
     */
    public boolean checkFighter(PlayerFighter fighter) {
        for (UseEffect effect : useEffects) {
            if (!effect.checkFighter(fighter)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Apply the item use effects to the fighter
     */
    public void applyToFighter(PlayerFighter fighter) {
        for (UseEffect effect : useEffects) {
            effect.applyToFighter(fighter);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final UsableItem usable = (UsableItem) obj;

        return
            template.equals(usable.template)
            && useEffects.equals(usable.useEffects)
            && specials.equals(usable.specials)
        ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, useEffects, specials);
    }
}
