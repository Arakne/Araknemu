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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.StaticInvocationFighter;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Handle invocation of a static creature
 * Unlike {@link MonsterInvocationHandler}, the invoked monster does not appear in timeline, and cannot perform any action
 *
 * A new fighter will be created and added to fight but not in timeline
 *
 * Effect parameters :
 * - #1 (min) : monster id
 * - #2 (max) : grade number
 *
 * @see StaticInvocationFighter Invoked fighter
 */
public final class StaticInvocationHandler implements EffectHandler {
    private final MonsterService monsterService;
    private final FighterFactory fighterFactory;
    private final Fight fight;

    public StaticInvocationHandler(MonsterService monsterService, FighterFactory fighterFactory, Fight fight) {
        this.monsterService = monsterService;
        this.fighterFactory = fighterFactory;
        this.fight = fight;
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("StaticInvocationHandler cannot be used as buff");
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter invocation = fighterFactory.generate(id -> new StaticInvocationFighter(
            id,
            monsterService.load(effect.effect().min()).get(effect.effect().max()),
            cast.caster().team(),
            cast.caster()
        ));

        fight.fighters().join(invocation, cast.target());
        invocation.init();

        fight.send(ActionEffect.addStaticInvocation(cast.caster(), invocation));
    }
}
