package fr.quatrevieux.araknemu.game.fight.module.util;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;

/**
 * Subscribe to events when fight is started, and remove the listener when stopped
 */
final public class ActiveFightSubscriber implements EventsSubscriber {
    final private Listener[] listeners;

    public ActiveFightSubscriber(Listener[] listeners) {
        this.listeners = listeners;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FightStarted>() {
                @Override
                public void on(FightStarted event) {
                    final ListenerAggregate dispatcher = event.fight().dispatcher();

                    for (Listener listener : listeners) {
                        dispatcher.add(listener);
                    }
                }

                @Override
                public Class<FightStarted> event() {
                    return FightStarted.class;
                }
            },

            new Listener<FightStopped>() {
                @Override
                public void on(FightStopped event) {
                    final ListenerAggregate dispatcher = event.fight().dispatcher();

                    for (Listener listener : listeners) {
                        dispatcher.remove(listener.getClass());
                    }
                }

                @Override
                public Class<FightStopped> event() {
                    return FightStopped.class;
                }
            },
        };
    }
}
