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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Check count of actually invoked creatures of the current fighter
 */
public final class InvocationCountValidator implements CastConstraintValidator<Spell> {
    private final int[] invocationEffects;

    public InvocationCountValidator() {
        this(new int[] {180, 181});
    }

    public InvocationCountValidator(int[] invocationEffects) {
        this.invocationEffects = invocationEffects;
    }

    @Override
    public boolean check(Turn turn, Spell castable, FightCell target) {
        if (!isInvocationSpell(castable)) {
            return true;
        }

        final ActiveFighter fighter = turn.fighter();
        final int max = max(fighter);
        final int actual = (int) fighter.fight().turnList().fighters().stream() // Iterate only on active fighters (i.e. in turn list)
            .filter(other -> fighter.equals(other.invoker()))
            .count()
        ;

        return max > actual;
    }

    @Override
    public @Nullable Error validate(Turn turn, Spell castable, FightCell target) {
        return check(turn, castable, target) ? null : Error.cantCastMaxSummonedCreaturesReached(max(turn.fighter()));
    }

    private int max(PassiveFighter fighter) {
        return fighter.characteristics().get(Characteristic.MAX_SUMMONED_CREATURES);
    }

    private boolean isInvocationSpell(Spell spell) {
        for (SpellEffect effect : spell.effects()) {
            for (int effectId : invocationEffects) {
                if (effect.effect() == effectId) {
                    return true;
                }
            }
        }

        return false;
    }
}
