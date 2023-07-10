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
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations.CreateDoubleHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations.MonsterInvocationHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations.StaticInvocationHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.monster.MonsterService;

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
        handler.register(180, new CreateDoubleHandler(fighterFactory, fight));
        handler.register(181, new MonsterInvocationHandler(monsterService, fighterFactory, fight));

        handler.register(185, new StaticInvocationHandler(monsterService, fighterFactory, fight));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[0];
    }
}
