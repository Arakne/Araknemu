package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.state.FightState;

/**
 * The fight module is used to register new effects, or listeners on the fight for extends its capabilities
 * The module instance is dedicated to one fight instance, and can safely be used as state object
 */
public interface FightModule extends EventsSubscriber {
    public interface Factory {
        /**
         * Create the module for the given fight
         */
        public FightModule create(Fight fight);
    }

    /**
     * Register fight effects into the effect handle
     */
    default public void effects(EffectsHandler handler) {}

    /**
     * The fight has changed its current state
     */
    default public void stateChanged(FightState newState) {}
}
