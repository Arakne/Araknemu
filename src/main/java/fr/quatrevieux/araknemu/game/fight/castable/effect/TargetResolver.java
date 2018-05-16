package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Resolve effect targets
 *
 * TargetResolver resolver = new TargetResolve(effect, caster);
 *
 * resolver
 *     .area(target)
 *     .fighters(fighter -> applyTo(fighter))
 * ;
 */
final public class TargetResolver {
    final private Fighter caster;
    final private FightCell target;

    private Set<FightCell> targets;

    public TargetResolver(Fighter caster, FightCell target) {
        this.caster = caster;
        this.target = target;
        this.targets = Collections.singleton(target);
    }

    /**
     * Resolve the effect area cells
     *
     * @param area The effect area
     */
    public TargetResolver area(SpellEffectArea area) {
        targets = area.resolve(target, caster.cell());

        return this;
    }

    /**
     * Apply to fighters the effect
     *
     * @param applier Effect to apply
     */
    public TargetResolver fighters(Consumer<Fighter> applier) {
        targets
            .stream()
            .map(FightCell::fighter)
            .forEach(fighter -> fighter.ifPresent(applier))
        ;

        return this;
    }

    /**
     * Map a function to the fighters
     *
     * @param mapper Effect to apply
     */
    public <R> Stream<R> map(Function<Fighter, ? extends R> mapper) {
        return targets
            .stream()
            .map(FightCell::fighter)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(mapper)
        ;
    }
}
