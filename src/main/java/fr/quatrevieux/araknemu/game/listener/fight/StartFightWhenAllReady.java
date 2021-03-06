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

package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;

/**
 * Start the fight when all fighters are ready
 */
public final class StartFightWhenAllReady implements Listener<FighterReadyStateChanged> {
    private final Fight fight;
    private final PlacementState state;

    public StartFightWhenAllReady(Fight fight, PlacementState state) {
        this.fight = fight;
        this.state = state;
    }

    @Override
    public void on(FighterReadyStateChanged event) {
        if (event.ready() && fight.fighters().stream().allMatch(Fighter::ready)) {
            state.startFight();
        }
    }

    @Override
    public Class<FighterReadyStateChanged> event() {
        return FighterReadyStateChanged.class;
    }
}
