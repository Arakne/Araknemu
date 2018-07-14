package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
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
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        if (!cast.target().walkable()) {
            return; // @todo exception ?
        }

        cast.caster().move(cast.target());

        fight.send(ActionEffect.teleport(cast.caster(), cast.caster(), cast.target()));
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use Teleport as buff effect");
    }
}
