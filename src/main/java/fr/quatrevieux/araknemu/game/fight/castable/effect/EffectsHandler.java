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

package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handle fight effects
 */
public final class EffectsHandler {
    private final Map<Integer, EffectHandler> handlers = new HashMap<>();

    public void register(int effectId, EffectHandler applier) {
        handlers.put(effectId, applier);
    }

    /**
     * Apply a cast to the fight
     *
     * First, this method will call {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook#onCast(Buff, CastScope)} to caster
     * Then call {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook#onCastTarget(Buff, CastScope)} to all targets
     *
     * After that, all effects will be applied by calling :
     * - {@link EffectHandler#handle(CastScope, CastScope.EffectScope)} if duration is 0
     * - {@link EffectHandler#buff(CastScope, CastScope.EffectScope)} if the effect has a duration
     */
    public void apply(CastScope<Fighter> cast) {
        cast.caster().buffs().onCast(cast);

        applyCastTarget(cast);

        for (CastScope<Fighter>.EffectScope effect : cast.effects()) {
            final EffectHandler handler = handlers.get(effect.effect().effect());
            // @todo Warning if handler is not found
            if (handler != null) {
                if (effect.effect().duration() == 0) {
                    handler.handle(cast, effect);
                } else {
                    handler.buff(cast, effect);
                }
            }
        }
    }

    /**
     * Call {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onCastTarget(CastScope)}
     * on each target.
     *
     * If a target is changed (by calling {@link CastScope#replaceTarget(PassiveFighter, PassiveFighter)}),
     * new targets will also be called
     */
    private void applyCastTarget(CastScope<Fighter> cast) {
        Set<Fighter> visitedTargets = Collections.emptySet();

        for (;;) {
            final Set<Fighter> currentTargets = cast.targets();

            boolean hasChanged = false;

            for (Fighter target : currentTargets) {
                // Ignore already called targets
                if (!visitedTargets.contains(target)) {
                    if (!target.buffs().onCastTarget(cast)) {
                        // The hook notify a target change
                        hasChanged = true;
                    }
                }
            }

            // There is no new targets, we can stop here
            if (!hasChanged) {
                return;
            }

            // cast#targets() always contains all resolved targets, including removed ones
            // so simple change visitedTargets by this value is enough to keep track of all already called fighters
            visitedTargets = currentTargets;
        }
    }
}
