package fr.quatrevieux.araknemu.game.listener.fight.turn.action;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionStarted;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FightAction;
import fr.quatrevieux.araknemu.network.game.out.fight.action.StartFightAction;

/**
 * Send the fight action
 */
final public class SendFightAction implements Listener<FightActionStarted> {
    final private Fight fight;

    public SendFightAction(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FightActionStarted event) {
        if (event.action().performer() instanceof Sender) {
            Sender.class.cast(event.action().performer()).send(new StartFightAction(event.action()));
        }

        fight.send(new FightAction(event.result()));
    }

    @Override
    public Class<FightActionStarted> event() {
        return FightActionStarted.class;
    }
}
