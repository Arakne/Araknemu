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

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.RaulebaqueHandler;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Module for handle Raulebaque spell
 *
 * This spell will reset all fighters position to the initial one
 */
public final class RaulebaqueModule implements FightModule {
    private final Fight fight;

    private @MonotonicNonNull Map<Fighter, FightCell> startPositions;

    public RaulebaqueModule(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(784, new RaulebaqueHandler(fight, this));
    }

    /**
     * Get all start positions of fighters
     * The map key is the fighter, and the value is the start cell
     */
    public Map<Fighter, FightCell> startPositions() {
        if (startPositions == null) {
            throw new IllegalStateException("Positions must be loaded at fight start");
        }

        return startPositions;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FightStarted>() {
                @Override
                public void on(FightStarted event) {
                    loadStartPositions();
                }

                @Override
                public Class<FightStarted> event() {
                    return FightStarted.class;
                }
            },
        };
    }

    /**
     * Load the start positions
     */
    private void loadStartPositions() {
        startPositions = new HashMap<>();

        for (Fighter fighter : fight.fighters()) {
            startPositions.put(fighter, fighter.cell());
        }
    }
}
