package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsUtils;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

import java.util.Random;

/**
 * Handle spell return buff effect
 */
final public class SpellReturnHandler implements EffectHandler, BuffHook {
    final private Fight fight;

    final private Random RANDOM = new Random();

    public SpellReturnHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Spell return effect can be only used as buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onCastTarget(Buff buff, CastScope cast) {
        if (buff.target().equals(cast.caster()) || !isReturnableCast(cast)) {
            return;
        }

        cast.spell().ifPresent(spell -> {
            if (!checkSpellReturned(spell, buff.effect())) {
                fight.send(ActionEffect.returnSpell(buff.target(), false));
                return;
            }

            cast.replaceTarget(buff.target(), cast.caster());
            fight.send(ActionEffect.returnSpell(buff.target(), true));
        });
    }

    /**
     * Check if the action is returnable (Contains damage or AP loose effects)
     *
     * @param cast The action to check
     *
     * @return true if the cast can is returnable
     */
    private boolean isReturnableCast(CastScope cast) {
        return cast.effects().stream()
            .map(CastScope.EffectScope::effect)
            .anyMatch(effect -> {
                // Should return only direct damage effects
                if (EffectsUtils.isDamageEffect(effect.effect()) && effect.duration() == 0) {
                    return true;
                }

                return EffectsUtils.isLooseApEffect(effect.effect());
            })
        ;
    }

    /**
     * Check if the spell should be returned
     *
     * Check the spell level and the return probability
     *
     * @param spell Spell to check
     * @param returnEffect The buff effect
     *
     * @return true if the spell should be returned
     */
    private boolean checkSpellReturned(Spell spell, SpellEffect returnEffect) {
        if (spell.level() > Math.max(returnEffect.min(), returnEffect.max())) {
            return false;
        }

        if (returnEffect.special() == 100) {
            return true;
        }

        return RANDOM.nextInt(100) < returnEffect.special();
    }
}
