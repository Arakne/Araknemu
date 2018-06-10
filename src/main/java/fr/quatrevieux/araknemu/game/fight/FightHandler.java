package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.builder.FightBuilder;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;

import java.util.function.Consumer;

/**
 * Handle fight creation
 */
final public class FightHandler<B extends FightBuilder> implements EventsSubscriber {
    final private FightService service;
    final private B builder;

    public FightHandler(FightService service, B builder) {
        this.service = service;
        this.builder = builder;
    }

    /**
     * Create and start the fight
     *
     * @param configuration The fight configuration
     */
    public Fight start(Consumer<B> configuration) {
        configuration.accept(builder);

        Fight fight = builder.build(service.newFightId());
        fight.nextState();

        fight.dispatcher().register(this);

        service.created(fight);

        return fight;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FightStopped>() {
                @Override
                public void on(FightStopped event) {
                    service.remove(event.fight());
                }

                @Override
                public Class<FightStopped> event() {
                    return FightStopped.class;
                }
            },
            new Listener<FightCancelled>() {
                @Override
                public void on(FightCancelled event) {
                    service.remove(event.fight());
                }

                @Override
                public Class<FightCancelled> event() {
                    return FightCancelled.class;
                }
            }
        };
    }
}
