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
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

/**
 * Apply spell boost
 */
public final class BoostSpellEffect implements SpecialEffectHandler {
    private final SpellsBoosts.Modifier modifier;

    public BoostSpellEffect(SpellsBoosts.Modifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void apply(SpecialEffect effect, GamePlayer player) {
        player.properties().spells().boosts().boost(
            effect.arguments()[0],
            modifier,
            effect.arguments()[2]
        );
    }

    @Override
    public void relieve(SpecialEffect effect, GamePlayer player) {
        player.properties().spells().boosts().boost(
            effect.arguments()[0],
            modifier,
            -effect.arguments()[2]
        );
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
