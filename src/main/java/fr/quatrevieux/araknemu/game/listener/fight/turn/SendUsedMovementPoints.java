package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.MovementPointsUsed;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Send to clients the used movement points
 */
final public class SendUsedMovementPoints implements Listener<MovementPointsUsed> {
    final private Fight fight;

    public SendUsedMovementPoints(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(MovementPointsUsed event) {
        fight.send(ActionEffect.usedMovementPoints(event.fighter(), event.quantity()));
    }

    @Override
    public Class<MovementPointsUsed> event() {
        return MovementPointsUsed.class;
    }
}
