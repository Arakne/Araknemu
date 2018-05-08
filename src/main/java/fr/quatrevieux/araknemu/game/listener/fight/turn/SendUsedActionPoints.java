package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.ActionPointsUsed;
import fr.quatrevieux.araknemu.game.fight.turn.event.MovementPointsUsed;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Send to clients the used action points
 */
final public class SendUsedActionPoints implements Listener<ActionPointsUsed> {
    final private Fight fight;

    public SendUsedActionPoints(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(ActionPointsUsed event) {
        fight.send(ActionEffect.usedActionPoints(event.fighter(), event.quantity()));
    }

    @Override
    public Class<ActionPointsUsed> event() {
        return ActionPointsUsed.class;
    }
}
