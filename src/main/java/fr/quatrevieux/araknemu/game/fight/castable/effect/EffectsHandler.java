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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handle fight effects
 */
public final class EffectsHandler implements CastConstraintValidator<Castable> {
    private final Fight fight;
    private final Map<Integer, EffectHandler> handlers = new HashMap<>();

    public EffectsHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public boolean check(Turn turn, Castable castable, BattlefieldCell target) {
        for (SpellEffect effect : castable.effects()) {
            final EffectHandler handler = handlers.get(effect.effect());

            if (handler != null) {
                if (!handler.check(turn, castable, target)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable Object validate(Turn turn, Castable castable, BattlefieldCell target) {
        for (SpellEffect effect : castable.effects()) {
            final EffectHandler handler = handlers.get(effect.effect());

            if (handler != null) {
                final Object error = handler.validate(turn, castable, target);

                if (error != null) {
                    return error;
                }
            }
        }

        return null;
    }

    public void register(int effectId, EffectHandler applier) {
        handlers.put(effectId, applier);
    }

    /**
     * Apply a cast to the fight
     *
     * First, this method will call {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook#onCast(Buff, FightCastScope)} to caster
     * Then call {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook#onCastTarget(Buff, FightCastScope)} to all targets
     *
     * After that, all effects will be applied by calling :
     * - {@link EffectHandler#handle(FightCastScope, FightCastScope.EffectScope)} if duration is 0
     * - {@link EffectHandler#buff(FightCastScope, FightCastScope.EffectScope)} if the effect has a duration
     */
    public void apply(FightCastScope cast) {
        cast.caster().buffs().onCast(cast);

        applyCastTarget(cast);

        for (FightCastScope.EffectScope effect : cast.effects()) {
            final EffectHandler handler = handlers.get(effect.effect().effect());
            // @todo Warning if handler is not found
            if (handler != null) {
                if (effect.effect().duration() == 0) {
                    handler.handle(cast, effect);
                } else {
                    handler.buff(cast, effect);
                }

                // Do not apply next effects if the fight is finished
                if (!fight.active()) {
                    break;
                }
            }
        }
    }

    /**
     * Call {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onCastTarget(FightCastScope)}
     * on each target.
     *
     * If a target is changed (by calling {@link CastScope#replaceTarget(FighterData, FighterData)}),
     * new targets will also be called
     */
    private void applyCastTarget(FightCastScope cast) {
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
