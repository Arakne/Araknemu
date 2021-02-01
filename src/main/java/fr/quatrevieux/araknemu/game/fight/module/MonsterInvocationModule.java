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
 * Copyright (c) 2017-2021 Vincent Quatrevieux Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations.MonsterInvocationHandler;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.monster.MonsterService;

public class MonsterInvocationModule implements FightModule {
    final private MonsterService monsterService;
    final private Fight fight;

    public MonsterInvocationModule(MonsterService monsterService, Fight fight) {
        this.monsterService = monsterService;
        this.fight = fight;
    }

    @Override
    public void effects(EffectsHandler handler) {
        // moving creatures
        handler.register(181, new MonsterInvocationHandler(monsterService, fight));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FighterDie>() {
                @Override
                public void on(FighterDie event) {
                    fight.fighters().forEach(fighter -> {
                        if (fighter.invoker().isPresent() && fighter.invoker().get().equals(event.fighter())) {
                            fighter.life().kill((ActiveFighter) event.fighter());
                            fight.turnList().remove(fighter);
                        }
                    });
                }

                @Override
                public Class<FighterDie> event() {
                    return FighterDie.class;
                }
            },

            new Listener<FightStopped>() {
                @Override
                public void on(FightStopped event) {
                    event.fight().fighters().forEach(fighter -> {
                        if (fighter.invoker().isPresent()) {
                            fighter.team().kick(fighter);
                        }
                    });
                }

                @Override
                public Class<FightStopped> event() {
                    return FightStopped.class;
                }
            },
        };
    }
}
