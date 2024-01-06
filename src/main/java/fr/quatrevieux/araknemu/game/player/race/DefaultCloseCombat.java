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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.race;

import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.adapter.SpellLevelConstraintAdapter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.List;

/**
 * The default close combat of a race
 */
public final class DefaultCloseCombat implements Castable {
    private final SpellTemplate.Level characteristics;
    private final List<SpellEffect> effects;
    private final List<SpellEffect> criticalEffects;
    private final SpellConstraints constraints;

    public DefaultCloseCombat(SpellTemplate.Level characteristics, List<SpellEffect> effects, List<SpellEffect> criticalEffects) {
        this.characteristics = characteristics;
        this.effects = effects;
        this.criticalEffects = criticalEffects;
        this.constraints = new SpellLevelConstraintAdapter(characteristics);
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
        return characteristics.apCost();
    }

    @Override
    public @NonNegative int criticalHit() {
        return characteristics.criticalHit();
    }

    @Override
    public @NonNegative int criticalFailure() {
        return characteristics.criticalFailure();
    }

    @Override
    public boolean modifiableRange() {
        return characteristics.modifiableRange();
    }

    @Override
    public SpellConstraints constraints() {
        return constraints;
    }
}
