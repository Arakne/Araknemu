package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Buff effect for removing action points
 * If this effect is not used as buff, it will remove movement points to the current turn
 */
final public class RemoveMovementPointsHandler extends RemoveCharacteristicHandler {
    final private Fight fight;

    public RemoveMovementPointsHandler(Fight fight) {
        super(fight, Characteristic.MOVEMENT_POINT);

        this.fight = fight;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        fight.turnList().current().ifPresent(turn -> {
            EffectValue value = new EffectValue(effect.effect());
            int mp = turn.points().removeMovementPoints(value.value());

            fight.send(ActionEffect.removeMovementPoints(turn.fighter(), mp));
        });
    }

    @Override
    public void onBuffStarted(Buff buff) {
        super.onBuffStarted(buff);

        fight.turnList().current()
            .filter(turn -> turn.fighter().equals(buff.target()))
            .ifPresent(turn -> turn.points().removeMovementPoints(buff.effect().min()))
        ;
    }
}
