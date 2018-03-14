package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Effect for learn a new spell
 */
final public class LearnSpellEffect implements UseEffectHandler {
    final private SpellService service;

    public LearnSpellEffect(SpellService service) {
        this.service = service;
    }

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        caster.spells().learn(
            service.get(effect.arguments()[2])
        );
    }

    @Override
    public void applyToTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {}

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        int spellId = effect.arguments()[2];

        if (!caster.spells().canLearn(service.get(spellId))) {
            caster.send(Error.cantLearnSpell(spellId));

            return false;
        }

        return true;
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        return false;
    }
}
