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
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.RemoveInvocationFromTurnList;
import fr.quatrevieux.araknemu.game.fight.fighter.event.RemoveInvocations;
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
        // static creatures
        handler.register(185, new MonsterInvocationHandler(monsterService, fight));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<RemoveInvocations>() {
                @Override
                public void on(RemoveInvocations event) {
                    fight.fighters().forEach(fighter -> {
                        if (fighter.invoker().isPresent() && fighter.invoker().get().equals(event.invoker())) {
                            fighter.life().kill((ActiveFighter)event.invoker());

                            fight.dispatch(new RemoveInvocationFromTurnList(fighter));
                        }
                    });
                }

                @Override
                public Class<RemoveInvocations> event() {
                    return RemoveInvocations.class;
                }
            },

            new Listener<RemoveInvocationFromTurnList>() {
                @Override
                public void on(RemoveInvocationFromTurnList event) {
                    if (event.fighter().invoker().isPresent()) {
                        fight.team(event.fighter().team().number()).kick((Fighter)event.fighter());
                        fight.turnList().remove((Fighter)event.fighter());
                    }
                }

                @Override
                public Class<RemoveInvocationFromTurnList> event() {
                    return RemoveInvocationFromTurnList.class;
                }
            },
        };
    }
}
