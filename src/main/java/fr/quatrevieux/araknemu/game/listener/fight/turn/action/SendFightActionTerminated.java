package fr.quatrevieux.araknemu.game.listener.fight.turn.action;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionTerminated;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FinishFightAction;

/**
 * Send to clients a terminated fight action
 */
final public class SendFightActionTerminated implements Listener<FightActionTerminated> {
    @Override
    public void on(FightActionTerminated event) {
        if (event.action().performer() instanceof Sender) {
            Sender.class.cast(event.action().performer()).send(new FinishFightAction(event.action()));
        }
    }

    @Override
    public Class<FightActionTerminated> event() {
        return FightActionTerminated.class;
    }
}
