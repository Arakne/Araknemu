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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMaxLifeChanged;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.IntRange;

/**
 * Handle life points for fighters
 */
public final class BaseFighterLife implements FighterLife {
    private final Fighter fighter;

    private @NonNegative int max;
    private @NonNegative int current;
    private @IntRange(from = 0, to = 100) int erosion = 0;
    private boolean dead = false;

    public BaseFighterLife(Fighter fighter, @NonNegative int life, @NonNegative int max) {
        this.max = max;
        this.current = life;
        this.fighter = fighter;
    }

    public BaseFighterLife(Fighter fighter, @NonNegative int life) {
        this(fighter, life, life);
    }

    @Override
    public @NonNegative int current() {
        return current;
    }

    @Override
    public @NonNegative int max() {
        return max;
    }

    @Override
    public boolean dead() {
        return dead;
    }

    @Override
    public @NonNegative int heal(Fighter caster, @NonNegative int value) {
        if (dead) {
            return 0;
        }

        final int current = this.current;
        final int left = Math.max(max - current, 0);
        final int actualHeal = Math.min(value, left);

        this.current = current + actualHeal;

        fighter.fight().dispatch(new FighterLifeChanged(fighter, caster, actualHeal));
        fighter.buffs().onHealApplied(actualHeal);

        return actualHeal;
    }

    @Override
    public @NonNegative int damage(Fighter caster, @NonNegative int value, @NonNegative int baseDamage) {
        if (dead) {
            return 0;
        }

        final int current = this.current;
        final int actualDamage = Math.min(value, current);
        final int newLife = Asserter.castNonNegative(current - actualDamage);

        // Apply erosion
        max = Math.max(max - (erosion * baseDamage / 100), 1);

        this.current = Math.min(newLife, max);
        fighter.fight().dispatch(new FighterLifeChanged(fighter, caster, -actualDamage));

        if (newLife == 0) {
            dead = true;
            fighter.fight().dispatch(new FighterDie(fighter, caster));
        } else {
            fighter.buffs().onDamageApplied(actualDamage);
        }

        return actualDamage;
    }

    @Override
    public void alterMax(Fighter caster, int value) {
        if (dead) {
            return;
        }

        current = Math.max(0, current + value);
        max = Math.max(0, max + value);

        if (current == 0) {
            dead = true;
            fighter.fight().dispatch(new FighterDie(fighter, caster));
        } else {
            fighter.fight().dispatch(new FighterMaxLifeChanged(fighter, caster));
        }
    }

    @Override
    public void kill(Fighter caster) {
        if (dead) {
            return;
        }

        current = 0;
        dead = true;
        fighter.fight().dispatch(new FighterDie(fighter, caster));
    }

    @Override
    public void resuscitate(Fighter caster, @Positive int value) {
        if (!dead) {
            return;
        }

        current = Math.min(value, max);
        dead = false;
    }

    @Override
    public void alterErosion(int value) {
        erosion = Math.max(0, Math.min(100, erosion + value));
    }
}
