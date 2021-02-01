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
 * Set fixed value for spell modifier
 */
public final class SetSpellModifierEffect implements SpecialEffectHandler {
    private final SpellsBoosts.Modifier modifier;

    public SetSpellModifierEffect(SpellsBoosts.Modifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void apply(SpecialEffect effect, GamePlayer player) {
        final SpellsBoosts boosts = player.properties().spells().boosts();

        final int spell = effect.arguments()[0];
        final int value = effect.arguments()[2];

        if (!boosts.modifiers(spell).has(modifier) || boosts.modifiers(spell).value(modifier) > value) {
            boosts.set(spell, modifier, value);
        }
    }

    @Override
    public void relieve(SpecialEffect effect, GamePlayer player) {
        final SpellsBoosts boosts = player.properties().spells().boosts();

        final int spell = effect.arguments()[0];
        final int value = effect.arguments()[2];

        if (boosts.modifiers(spell).value(modifier) == value) {
            boosts.unset(spell, modifier);
        }
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
