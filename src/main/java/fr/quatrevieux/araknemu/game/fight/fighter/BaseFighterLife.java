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
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Handle life points for fighters
 */
public final class BaseFighterLife implements FighterLife {
    private final Fighter fighter;

    private @NonNegative int max;
    private @NonNegative int current;
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
    @SuppressWarnings("compound.assignment") // bound of value is not resolved
    public int alter(FighterData caster, int value) {
        if (dead) {
            return 0;
        }

        if (value < -current) {
            value = -current;
        } else if (value > max - current) {
            value = max - current;
        }

        current += value;

        fighter.fight().dispatch(new FighterLifeChanged(fighter, caster, value));

        if (current == 0) {
            dead = true;
            fighter.fight().dispatch(new FighterDie(fighter, caster));
        } else {
            fighter.buffs().onLifeAltered(value);
        }

        return value;
    }

    @Override
    public void alterMax(FighterData caster, int value) {
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
    public void kill(FighterData caster) {
        if (dead) {
            return;
        }

        current = 0;
        dead = true;
        fighter.fight().dispatch(new FighterDie(fighter, caster));
    }
}
