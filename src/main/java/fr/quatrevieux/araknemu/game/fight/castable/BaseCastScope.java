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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wrap casting arguments
 */
public class BaseCastScope<F extends FighterData, C extends BattlefieldCell> implements CastScope<F, C> {
    private final Castable action;
    private final F caster;
    private final C from;
    private final C target;

    private final List<EffectScope> effects;
    private final Map<F, @Nullable F> targetMapping = new HashMap<>();

    @SuppressWarnings("unchecked")
    protected BaseCastScope(Castable action, F caster, C target, List<SpellEffect> effects) {
        this(action, caster, (C) caster.cell(), target, effects);
    }

    protected BaseCastScope(Castable action, F caster, C from, C target, List<SpellEffect> effects) {
        this.action = action;
        this.caster = caster;
        this.target = target;
        this.from = from;

        this.effects = effects.stream()
            .map(effect -> new EffectScope(effect, CastTargetResolver.resolveFromEffect(caster, from, target, action, effect)))
            .collect(Collectors.toList())
        ;

        for (EffectScope effect : this.effects) {
            for (F fighter : effect.targets) {
                this.targetMapping.put(fighter, fighter);
            }
        }
    }

    @Override
    @Pure
    public final Castable action() {
        return action;
    }

    @Override
    @Pure
    public final @Nullable Spell spell() {
        if (action instanceof Spell) {
            return (Spell) action;
        } else {
            return null;
        }
    }

    @Override
    @Pure
    public final F caster() {
        return caster;
    }

    @Override
    @Pure
    public C from() {
        return from;
    }

    @Override
    @Pure
    public final C target() {
        return target;
    }

    @Override
    public final Set<F> targets() {
        return new HashSet<>(targetMapping.keySet());
    }

    /**
     * Replace a target of the cast
     *
     * @param originalTarget The base target fighter
     * @param newTarget The new target fighter
     */
    public final void replaceTarget(F originalTarget, F newTarget) {
        targetMapping.put(originalTarget, newTarget);

        // Add new target as target if not yet defined
        if (!targetMapping.containsKey(newTarget)) {
            targetMapping.put(newTarget, newTarget);
        }
    }

    /**
     * Remove a target of the cast
     *
     * Note: this method will definitively remove the target,
     * even if {@link BaseCastScope#replaceTarget(FighterData, FighterData)} is called
     */
    public final void removeTarget(F target) {
        // Set target to null without remove the key to ensure that it will effectively remove
        // even if a replaceTarget() point to it
        targetMapping.put(target, null);
    }

    @Override
    @Pure
    public final List<EffectScope> effects() {
        return effects;
    }

    /**
     * Resolve the target mapping
     *
     * @param baseTarget The base target of the effect
     *
     * @return Resolved target. Null if the target is removed
     */
    private @Nullable F resolveTarget(F baseTarget) {
        F target = targetMapping.get(baseTarget);

        // Target is removed, or it's the original one : do not resolve chaining
        if (target == null || target.equals(baseTarget)) {
            return target;
        }

        // Keep list of visited mapping to break recursion
        final Set<F> resolved = new HashSet<>();

        resolved.add(baseTarget);
        resolved.add(target);

        // Resolve chaining
        for (;;) {
            target = targetMapping.get(target);

            // The target is removed, or already visited (can be itself)
            if (target == null || resolved.contains(target)) {
                return target;
            }

            resolved.add(target);
        }
    }

    public final class EffectScope implements CastScope.EffectScope<F, C> {
        private final SpellEffect effect;
        private final Collection<F> targets;

        public EffectScope(SpellEffect effect, Collection<F> targets) {
            this.effect = effect;
            this.targets = targets;
        }

        /**
         * The related effect
         */
        @Pure
        public SpellEffect effect() {
            return effect;
        }

        /**
         * Get all targeted fighters for the current effect
         */
        public Collection<F> targets() {
            F firstTarget = null;
            Collection<F> resolvedTargets = null;

            for (F baseTarget : targets) {
                final F resolved = resolveTarget(baseTarget);

                if (resolved == null || resolved.dead()) {
                    continue;
                }

                if (firstTarget == null) {
                    firstTarget = resolved;
                } else {
                    if (resolvedTargets == null) {
                        resolvedTargets = new ArrayList<>();
                        resolvedTargets.add(firstTarget);
                    }

                    resolvedTargets.add(resolved);
                }
            }

            if (resolvedTargets != null) {
                return resolvedTargets;
            }

            if (firstTarget != null) {
                return Collections.singleton(firstTarget);
            }

            return Collections.emptyList();
        }

        @Override
        public Set<C> cells() {
            return effect.area().resolve(target, from);
        }
    }
}
