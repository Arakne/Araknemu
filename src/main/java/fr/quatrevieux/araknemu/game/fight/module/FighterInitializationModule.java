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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;

/**
 * Module for perform initialization of fighters depending on configuration
 */
public final class FighterInitializationModule implements FightModule {
    private final GameConfiguration.FightConfiguration configuration;

    public FighterInitializationModule(GameConfiguration.FightConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FighterInitialized>() {
                @Override
                public void on(FighterInitialized event) {
                    initializeFighter(event.fighter());
                }

                @Override
                public Class<FighterInitialized> event() {
                    return FighterInitialized.class;
                }
            },
        };
    }

    private void initializeFighter(Fighter fighter) {
        fighter.life().alterErosion(configuration.initialErosion());
    }
}
