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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.CarryHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.CarryingApplier;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.ThrowHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMoved;

/**
 * Module for provide carry and throw effects
 */
public final class CarryingModule implements FightModule {
    private final Fight fight;
    private final CarryingApplier applier;

    public CarryingModule(Fight fight) {
        this.fight = fight;
        this.applier = new CarryingApplier(fight, 3, 8);
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(50, new CarryHandler(applier));
        handler.register(51, new ThrowHandler(applier));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FighterMoved>() {
                @Override
                public void on(FighterMoved event) {
                    applier.synchronizeMove(event.fighter());
                }

                @Override
                public Class<FighterMoved> event() {
                    return FighterMoved.class;
                }
            },
            new Listener<FighterDie>() {
                @Override
                public void on(FighterDie event) {
                    final Fighter fighter = event.fighter();

                    // End of carry effect must be applied after all die events are applied
                    // To ensure that battlefield is correctly cleaned
                    if (applier.active(fighter)) {
                        fight.execute(() -> applier.stop(fighter));
                    }
                }

                @Override
                public Class<FighterDie> event() {
                    return FighterDie.class;
                }
            },
        };
    }
}
