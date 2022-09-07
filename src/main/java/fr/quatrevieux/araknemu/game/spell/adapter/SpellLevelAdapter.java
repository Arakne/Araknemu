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

package fr.quatrevieux.araknemu.game.spell.adapter;

import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

import java.util.List;

/**
 * Adapt {@link fr.quatrevieux.araknemu.data.world.entity.SpellTemplate.Level} to {@link Spell}
 */
public final class SpellLevelAdapter implements Spell {
    private final @Positive int level;
    private final SpellTemplate template;
    private final SpellTemplate.Level data;
    private final List<SpellEffect> effects;
    private final List<SpellEffect> criticalEffects;
    private final SpellLevelConstraintAdapter constraints;

    public SpellLevelAdapter(@Positive int level, SpellTemplate template, SpellTemplate.Level data, List<SpellEffect> effects, List<SpellEffect> criticalEffects) {
        this.level = level;
        this.template = template;
        this.data = data;
        this.effects = effects;
        this.criticalEffects = criticalEffects;
        this.constraints = new SpellLevelConstraintAdapter(data);
    }

    @Override
    public int id() {
        return template.id();
    }

    @Override
    public int spriteId() {
        return template.sprite();
    }

    @Override
    public String spriteArgs() {
        return template.spriteArgs();
    }

    @Override
    public @Positive int level() {
        return level;
    }

    @Override
    public List<SpellEffect> effects() {
        return effects;
    }

    @Override
    public List<SpellEffect> criticalEffects() {
        return criticalEffects;
    }

    @Override
    public @NonNegative int apCost() {
        return data.apCost();
    }

    @Override
    public @NonNegative int criticalHit() {
        return data.criticalHit();
    }

    @Override
    public @NonNegative int criticalFailure() {
        return data.criticalFailure();
    }

    @Override
    public boolean modifiableRange() {
        return data.modifiableRange();
    }

    @Override
    public int minPlayerLevel() {
        return data.minPlayerLevel();
    }

    @Override
    public boolean endsTurnOnFailure() {
        return data.endsTurnOnFailure();
    }

    @Override
    public SpellConstraints constraints() {
        return constraints;
    }
}
