/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

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
    /**
     * Register fight effects into the effect handle
     */
    public default void effects(EffectsHandler handler) {}

    /**
     * The fight has changed its current state
     */
    public default void stateChanged(FightState newState) {}

    public static interface Factory {
        /**
         * Create the module for the given fight
         */
        public FightModule create(Fight fight);
    }
}
