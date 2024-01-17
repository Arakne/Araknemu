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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.BaseCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Base implementation for effect handler which apply effect on each target
 * with a single effect value dice roll (i.e. all targets have the same value)
 *
 * Only method {@link EffectHandler#handle(FightCastScope, BaseCastScope.EffectScope)} is implemented, and marked as final.
 * So you need to implement :
 * - {@link EffectHandler#buff(FightCastScope, BaseCastScope.EffectScope)} for applying the effect as buff
 * - {@link AbstractPreRollEffectHandler#applyOnTarget(FightCastScope, SpellEffect, Fighter, EffectValue)} for applying the effect on each target
 *
 * Note: The application of the effect is stopped if the fight is not active anymore (e.g. last target died by the effect)
 */
public abstract class AbstractPreRollEffectHandler implements EffectHandler {
    private final Fight fight;

    public AbstractPreRollEffectHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public final void handle(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        final Fight fight = this.fight;
        final SpellEffect spellEffect = effect.effect();
        final EffectValue.Context context = EffectValue.preRoll(spellEffect, cast.caster());

        for (Fighter target : effect.targets()) {
            if (!fight.active()) {
                break;
            }

            applyOnTarget(cast, spellEffect, target, context.forTarget(target));
        }
    }

    /**
     * Apply the effect on the target
     *
     * @param cast The cast action arguments
     * @param effect The spell effect to apply
     * @param target The target of the effect
     * @param value The pre-roll value. Buff hooks are already applied
     *
     * @see EffectValue#preRoll(SpellEffect, FighterData) Called to get the pre-roll value
     * @see EffectValue.Context#forTarget(FighterData) Called to get the effect value instance for the target
     */
    protected abstract void applyOnTarget(FightCastScope cast, SpellEffect effect, Fighter target, EffectValue value);
}
