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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wrap casting arguments
 */
final public class CastScope {
    public class EffectScope {
        final private SpellEffect effect;
        final private Collection<Fighter> targets;

        public EffectScope(SpellEffect effect, Collection<Fighter> targets) {
            this.effect = effect;
            this.targets = targets;
        }

        public SpellEffect effect() {
            return effect;
        }

        public Collection<Fighter> targets() {
            return targets.stream()
                .map(targetMapping::get)
                .filter(fighter -> !fighter.dead())
                .collect(Collectors.toList())
            ;
        }
    }

    /**
     * Cast scope is a temporary object, and the random is rarely used (only for "probable effects")
     */
    final static private RandomUtil RANDOM = RandomUtil.createShared();

    final private Castable action;
    final private Fighter caster;
    final private FightCell target;

    private List<EffectScope> effects;
    private Map<Fighter, Fighter> targetMapping;

    public CastScope(Castable action, Fighter caster, FightCell target) {
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
    public Fighter caster() {
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
     */
    public Set<Fighter> targets() {
        return new HashSet<>(targetMapping.values());
    }

    /**
     * Replace a target of the cast
     *
     * @param originalTarget The base target fighter
     * @param newTarget The new target fighter
     */
    public void replaceTarget(Fighter originalTarget, Fighter newTarget) {
        targetMapping.put(originalTarget, newTarget);
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
            for (Fighter fighter : effect.targets) {
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
        List<SpellEffect> selectedEffects = new ArrayList<>(effects.size());
        List<SpellEffect> probableEffects = new ArrayList<>();

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
    private Collection<Fighter> resolveTargets(SpellEffect effect) {
        if (effect.target().onlyCaster()) {
            return Collections.singleton(caster);
        }

        if (action.constraints().freeCell()) {
            return Collections.emptyList();
        }

        return effect.area()
            .resolve(target, caster.cell())
            .stream()
            .map(FightCell::fighter)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(fighter -> effect.target().test(caster, fighter))
            .collect(Collectors.toList())
        ;
    }
}
