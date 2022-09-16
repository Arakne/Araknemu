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
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope.EffectScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;

/**
 * Handle monster invocation
 *
 * A new fighter will be created and added to fight and timeline (turn list)
 *
 * Effect parameters :
 * - #1 (min) : monster id
 * - #2 (max) : grade number
 *
 * @see InvocationFighter Invoked fighter
 */
public final class MonsterInvocationHandler implements EffectHandler {
     private final MonsterService monsterService;
     private final FighterFactory fighterFactory;
     private final Fight fight;

    public MonsterInvocationHandler(MonsterService monsterService, FighterFactory fighterFactory, Fight fight) {
        this.monsterService = monsterService;
        this.fighterFactory = fighterFactory;
        this.fight = fight;
    }

    @Override
    public void buff(CastScope cast, EffectScope effect) {
        handle(cast, effect);
    }

    @Override
    public void handle(CastScope cast, EffectScope effect) {
        final Fighter invocation = fighterFactory.generate(id -> new InvocationFighter(
            id,
            monsterService.load(effect.effect().min()).get(effect.effect().max()),
            (FightTeam) cast.caster().team(),
            cast.caster()
        ));

        invocation.joinFight(fight, cast.target());
        fight.turnList().add(invocation);

        invocation.init();

        fight.send(ActionEffect.addInvocation(cast.caster(), invocation));
        fight.send(ActionEffect.packet(cast.caster(), new FighterTurnOrder(fight.turnList())));
    }
}
