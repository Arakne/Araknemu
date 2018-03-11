package fr.quatrevieux.araknemu.game.spell.adapter;

import fr.quatrevieux.araknemu.data.value.SpellTemplateEffect;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.SpellTemplateEffectAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapt {@link fr.quatrevieux.araknemu.data.world.entity.SpellTemplate.Level} to {@link Spell}
 */
final public class SpellLevelAdapter implements Spell {
    final private int level;
    final private SpellTemplate template;
    final private SpellTemplate.Level data;

    final private SpellLevelConstraintAdapter constraints;
    final private List<SpellEffect> effects;
    final private List<SpellEffect> criticalEffects;

    public SpellLevelAdapter(int level, SpellTemplate template, SpellTemplate.Level data) {
        this.level = level;
        this.template = template;
        this.data = data;

        constraints = new SpellLevelConstraintAdapter(data);
        effects = makeEffects(data.effects(), 0);
        criticalEffects = makeEffects(data.criticalEffects(), data.effects().size());
    }

    @Override
    public int id() {
        return template.id();
    }

    @Override
    public int level() {
        return level;
    }

    @Override
    public List<SpellEffect> effects() {
        return effects;
    }

    @Override
    public List<SpellEffect> criticalEffects() {
        return criticalEffects;
    }

    @Override
    public int apCost() {
        return data.apCost();
    }

    @Override
    public int criticalHit() {
        return data.criticalHit();
    }

    @Override
    public int criticalFailure() {
        return data.criticalFailure();
    }

    @Override
    public boolean modifiableRange() {
        return data.modifiableRange();
    }

    @Override
    public int minPlayerLevel() {
        return data.minPlayerLevel();
    }

    @Override
    public boolean endsTurnOnFailure() {
        return data.endsTurnOnFailure();
    }

    @Override
    public SpellConstraints constraints() {
        return constraints;
    }

    private List<SpellEffect> makeEffects(List<SpellTemplateEffect> templateEffects, int areaIndexStart) {
        List<SpellEffect> effects = new ArrayList<>(templateEffects.size());

        for (int i = 0; i < templateEffects.size(); ++i) {
            effects.add(
                new SpellTemplateEffectAdapter(
                    templateEffects.get(i),
                    data.effectAreas().get(i + areaIndexStart),
                    template.targets().length > i
                        ? template.targets()[i]
                        : 0
                )
            );
        }

        return effects;
    }
}
