package fr.quatrevieux.araknemu.game.fight.castable;

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

    final static private Random RANDOM = new Random();

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
