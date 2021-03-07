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

package fr.quatrevieux.araknemu.game.item.effect.special;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Null object for effect handler
 */
public final class NullEffectHandler implements SpecialEffectHandler {
    public static final SpecialEffectHandler INSTANCE = new NullEffectHandler();

    @Override
    public void apply(SpecialEffect effect, GamePlayer player) {
        // No op
    }

    @Override
    public void relieve(SpecialEffect effect, GamePlayer player) {
        // No op
    }

    @Override
    public SpecialEffect create(ItemTemplateEffectEntry entry, boolean maximize) {
        return new SpecialEffect(
            this,
            entry.effect(),
            new int[] {entry.min(), entry.max(), entry.special()},
            entry.text()
        );
    }
}
