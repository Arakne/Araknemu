package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Handle teleport effect
 */
final public class TeleportHandler implements EffectHandler {
    final private Fight fight;

    public TeleportHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(Fighter caster, Spell spell, SpellEffect effect, FightCell target) {
        if (!target.walkable()) {
            return; // @todo exception ?
        }

        caster.move(target);

        fight.send(ActionEffect.teleport(caster, caster, target));
    }
}
