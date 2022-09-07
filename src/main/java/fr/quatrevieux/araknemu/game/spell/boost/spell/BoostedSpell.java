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

package fr.quatrevieux.araknemu.game.spell.boost.spell;

import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.boost.SpellModifiers;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spell with modifiers
 */
public final class BoostedSpell implements Spell {
    private final Spell spell;
    private final SpellModifiers modifiers;

    public BoostedSpell(Spell spell, SpellModifiers modifiers) {
        this.spell = spell;
        this.modifiers = modifiers;
    }

    @Override
    public int id() {
        return spell.id();
    }

    @Override
    public int spriteId() {
        return spell.spriteId();
    }

    @Override
    public String spriteArgs() {
        return spell.spriteArgs();
    }

    @Override
    public @Positive int level() {
        return spell.level();
    }

    @Override
    public List<SpellEffect> effects() {
        return spell.effects()
            .stream()
            .map(e -> new BoostedSpellEffect(e, modifiers))
            .collect(Collectors.toList())
        ;
    }

    @Override
    public List<SpellEffect> criticalEffects() {
        return spell.criticalEffects()
            .stream()
            .map(e -> new BoostedSpellEffect(e, modifiers))
            .collect(Collectors.toList())
        ;
    }

    @Override
    public @NonNegative int apCost() {
        return Math.max(spell.apCost() - modifiers.apCost(), 0);
    }

    @Override
    public @NonNegative int criticalHit() {
        return Math.max(spell.criticalHit() - modifiers.criticalHit(), 0);
    }

    @Override
    public @NonNegative int criticalFailure() {
        return spell.criticalFailure();
    }

    @Override
    public boolean modifiableRange() {
        return spell.modifiableRange() || modifiers.modifiableRange();
    }

    @Override
    public int minPlayerLevel() {
        return spell.minPlayerLevel();
    }

    @Override
    public boolean endsTurnOnFailure() {
        return spell.endsTurnOnFailure();
    }

    @Override
    public SpellConstraints constraints() {
        return new BoostedSpellConstraints(spell.constraints(), modifiers);
    }
}
