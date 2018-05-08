package fr.quatrevieux.araknemu.game.spell.boost.spell;

import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.boost.SpellModifiers;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spell with modifiers
 */
final public class BoostedSpell implements Spell {
    final private Spell spell;
    final private SpellModifiers modifiers;

    public BoostedSpell(Spell spell, SpellModifiers modifiers) {
        this.spell = spell;
        this.modifiers = modifiers;
    }

    @Override
    public int id() {
        return spell.id();
    }

    @Override
    public int spriteId() {
        return spell.spriteId();
    }

    @Override
    public String spriteArgs() {
        return spell.spriteArgs();
    }

    @Override
    public int level() {
        return spell.level();
    }

    @Override
    public List<SpellEffect> effects() {
        return spell.effects()
            .stream()
            .map(e -> new BoostedSpellEffect(e, modifiers))
            .collect(Collectors.toList())
        ;
    }

    @Override
    public List<SpellEffect> criticalEffects() {
        return spell.criticalEffects()
            .stream()
            .map(e -> new BoostedSpellEffect(e, modifiers))
            .collect(Collectors.toList())
        ;
    }

    @Override
    public int apCost() {
        return spell.apCost() - modifiers.apCost();
    }

    @Override
    public int criticalHit() {
        return spell.criticalHit() - modifiers.criticalHit();
    }

    @Override
    public int criticalFailure() {
        return spell.criticalFailure();
    }

    @Override
    public boolean modifiableRange() {
        return spell.modifiableRange() || modifiers.modifiableRange();
    }

    @Override
    public int minPlayerLevel() {
        return spell.minPlayerLevel();
    }

    @Override
    public boolean endsTurnOnFailure() {
        return spell.endsTurnOnFailure();
    }

    @Override
    public SpellConstraints constraints() {
        return new BoostedSpellConstraints(spell.constraints(), modifiers);
    }
}
