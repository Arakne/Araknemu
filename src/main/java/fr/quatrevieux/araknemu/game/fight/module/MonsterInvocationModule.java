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
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations.StaticInvocationHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.monster.MonsterService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Module for enable invocation effects
 *
 * @see MonsterInvocationHandler
 */
public final class MonsterInvocationModule implements FightModule {
    private final MonsterService monsterService;
    private final FighterFactory fighterFactory;
    private final Fight fight;

    public MonsterInvocationModule(MonsterService monsterService, FighterFactory fighterFactory, Fight fight) {
        this.monsterService = monsterService;
        this.fighterFactory = fighterFactory;
        this.fight = fight;
    }

    @Override
    public void effects(EffectsHandler handler) {
        // moving creatures
        handler.register(181, new MonsterInvocationHandler(monsterService, fighterFactory, fight));
        handler.register(185, new StaticInvocationHandler(monsterService, fighterFactory, fight));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FighterDie>() {
                @Override
                public void on(FighterDie event) {
                    // Remove all invocations of the fighter
                    // Make a copy to ensure that no concurrent modification occur
                    final List<Fighter> invocations = fight.fighters().stream()
                        .filter(fighter -> event.fighter().equals(fighter.invoker()))
                        .collect(Collectors.toList())
                    ;

                    if (!invocations.isEmpty()) {
                        // Kill all invocations asynchronously
                        fight.execute(() -> invocations.forEach(fighter -> fighter.life().kill(event.caster())));
                    }

                    // If the creature is an invocation, delete from turn list
                    if (event.fighter().invoked()) {
                        fight.fighters().leave(event.fighter());
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
