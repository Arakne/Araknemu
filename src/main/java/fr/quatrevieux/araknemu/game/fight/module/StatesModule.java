package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.PushStateHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.RemoveStateHandler;
import fr.quatrevieux.araknemu.game.fight.module.util.ActiveFightSubscriber;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RefreshStates;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendState;

/**
 * Module for handle fighter states
 */
final public class StatesModule implements FightModule {
    final private Fight fight;
    final private EventsSubscriber subscriber;

    public StatesModule(Fight fight) {
        this.fight = fight;
        this.subscriber = new ActiveFightSubscriber(new Listener[] {
            new RefreshStates(),
            new SendState(fight)
        });
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(950, new PushStateHandler(fight));
        handler.register(951, new RemoveStateHandler());
    }

    @Override
    public Listener[] listeners() {
        return subscriber.listeners();
    }
}
