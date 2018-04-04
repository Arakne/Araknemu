package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightStarted;

/**
 * State for active fight
 */
final public class ActiveState implements FightState, EventsSubscriber {
    private Fight fight;

    @Override
    public void start(Fight fight) {
        this.fight = fight;
        fight.dispatcher().register(this);

        fight.dispatch(new FightStarted());
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new SendFightStarted(fight)
        };
    }

    @Override
    public int id() {
        return 3;
    }
}
