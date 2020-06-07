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

package fr.quatrevieux.araknemu.game.item.effect.special;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;

/**
 * Sub special effect to player {@link SpecialEffects}
 */
final public class SubSpecialEffect implements SpecialEffectHandler {
    final private SpecialEffects.Type type;

    final private RandomUtil random = new RandomUtil();

    public SubSpecialEffect(SpecialEffects.Type type) {
        this.type = type;
    }

    @Override
    public void apply(SpecialEffect effect, GamePlayer player) {
        player.properties().characteristics().specials().sub(type, effect.arguments()[0]);
    }

    @Override
    public void relieve(SpecialEffect effect, GamePlayer player) {
        player.properties().characteristics().specials().add(type, effect.arguments()[0]);
    }

    @Override
    public SpecialEffect create(ItemTemplateEffectEntry entry, boolean maximize) {
        int value = maximize ? entry.min() : random.rand(entry.min(), entry.max());

        return new SpecialEffect(this, entry.effect(), new int[] {value, 0, entry.special()}, "0d0+" + value);
    }
}
