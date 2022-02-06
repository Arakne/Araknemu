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

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wrap casting arguments
 */
public final class CastScope {
    /**
     * Cast scope is a temporary object, and the random is rarely used (only for "probable effects")
     */
    private static final RandomUtil RANDOM = RandomUtil.createShared();

    private final Castable action;
    private final ActiveFighter caster;
    private final FightCell target;

    private List<EffectScope> effects;
    private Map<PassiveFighter, PassiveFighter> targetMapping;

    public CastScope(Castable action, ActiveFighter caster, FightCell target) {
        this.action = action;
        this.caster = caster;
        this.target = target;
    }

    /**
     * Get the casted action
     */
    public Castable action() {
        return action;
    }

    /**
     * Get the spell, if and only if the action is a spell
     */
    public Optional<Spell> spell() {
        if (action instanceof Spell) {
            return Optional.of((Spell) action);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get the caster
     */
    public ActiveFighter caster() {
        return caster;
    }

    /**
     * Get the targeted cell
     */
    public FightCell target() {
        return target;
    }

    /**
     * Get the cast targets
     *
     * This method will not resolve target mapping, nor effect target mapping
     * It will return all targets, before the mapping is resolved
     * So if {@link CastScope#replaceTarget(PassiveFighter, PassiveFighter)} is called,
     * the new target will be added on this set
     *
     * Note: a new instance is returned to ensure that concurrent modification will not occur
     */
    public Set<PassiveFighter> targets() {
        return new HashSet<>(targetMapping.keySet());
    }

    /**
     * Replace a target of the cast
     *
     * @param originalTarget The base target fighter
     * @param newTarget The new target fighter
     */
    public void replaceTarget(PassiveFighter originalTarget, PassiveFighter newTarget) {
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
     * even if {@link CastScope#replaceTarget(PassiveFighter, PassiveFighter)} is called
     */
    public void removeTarget(PassiveFighter target) {
        // Set target to null without remove the key to ensure that it will effectively remove
        // even if a replaceTarget() point to it
        targetMapping.put(target, null);
    }

    /**
     * Get list of effects to apply
     */
    public List<EffectScope> effects() {
        return effects;
    }

    /**
     * Add effects to the cast scope
     *
     * @param effects Effects to add
     */
    public CastScope withEffects(List<SpellEffect> effects) {
        this.effects = effects.stream()
            .map(effect -> new EffectScope(effect, resolveTargets(effect)))
            .collect(Collectors.toList())
        ;

        targetMapping = new HashMap<>();

        for (EffectScope effect : this.effects) {
            for (PassiveFighter fighter : effect.targets) {
                targetMapping.put(fighter, fighter);
            }
        }

        return this;
    }

    /**
     * Add effects which can have a probability to occurs (ex: Bluff)
     *
     * @param effects Effects to resolve and add
     */
    public CastScope withRandomEffects(List<SpellEffect> effects) {
        final List<SpellEffect> selectedEffects = new ArrayList<>(effects.size());
        final List<SpellEffect> probableEffects = new ArrayList<>();

        for (SpellEffect effect : effects) {
            if (effect.probability() == 0) {
                selectedEffects.add(effect);
            }  else {
                probableEffects.add(effect);
            }
        }

        // No probable effects
        if (probableEffects.isEmpty()) {
            return withEffects(effects);
        }

        int dice = RANDOM.nextInt(100);

        for (SpellEffect effect : probableEffects) {
            dice -= effect.probability();

            if (dice <= 0) {
                selectedEffects.add(effect);
                break;
            }
        }

        return withEffects(selectedEffects);
    }

    /**
     * Resolve the targets of the effect
     */
    private Collection<PassiveFighter> resolveTargets(SpellEffect effect) {
        if (effect.target().onlyCaster()) {
            return Collections.singleton(caster);
        }

        if (action.constraints().freeCell()) {
            return Collections.emptyList();
        }

        // Use lazy instantiation and do not use stream API to optimise memory allocations
        PassiveFighter firstTarget = null;
        Collection<PassiveFighter> targets = null;

        for (FightCell cell : effect.area().resolve(target, caster.cell())) {
            final Optional<PassiveFighter> resolvedTarget = cell.fighter().filter(fighter -> effect.target().test(caster, fighter));

            if (!resolvedTarget.isPresent()) {
                continue;
            }

            // Found the first target
            if (firstTarget == null) {
                firstTarget = resolvedTarget.get();
                continue;
            }

            // Multiple targets are found : instantiate the collection
            if (targets == null) {
                targets = new ArrayList<>();
                targets.add(firstTarget);
            }

            targets.add(resolvedTarget.get());
        }

        // There is multiple targets
        if (targets != null) {
            return targets;
        }

        // There is only one target : create a singleton
        if (firstTarget != null) {
            return Collections.singleton(firstTarget);
        }

        // No targets are resolved
        return Collections.emptyList();
    }

    /**
     * Resolve the target mapping
     *
     * @param baseTarget The base target of the effect
     *
     * @return Resolved target. Null if the target is removed
     */
    private PassiveFighter resolveTarget(PassiveFighter baseTarget) {
        PassiveFighter target = targetMapping.get(baseTarget);

        // Target is removed, or it's the original one : do not resolve chaining
        if (target == null || target.equals(baseTarget)) {
            return target;
        }

        // Keep list of visited mapping to break recursion
        final Set<PassiveFighter> resolved = new HashSet<>();

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

    public final class EffectScope {
        private final SpellEffect effect;
        private final Collection<PassiveFighter> targets;

        public EffectScope(SpellEffect effect, Collection<PassiveFighter> targets) {
            this.effect = effect;
            this.targets = targets;
        }

        /**
         * The related effect
         */
        public SpellEffect effect() {
            return effect;
        }

        /**
         * Get all targeted fighters for the current effect
         */
        public Collection<PassiveFighter> targets() {
            return targets.stream()
                .map(CastScope.this::resolveTarget)
                .filter(fighter -> fighter != null && !fighter.dead())
                .collect(Collectors.toList())
            ;
        }
    }
}
