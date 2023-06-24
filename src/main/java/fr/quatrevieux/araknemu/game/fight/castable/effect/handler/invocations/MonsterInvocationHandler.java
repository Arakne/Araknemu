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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

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
public final class MonsterInvocationHandler extends AbstractInvocationHandler {
     private final MonsterService monsterService;

    public MonsterInvocationHandler(MonsterService monsterService, FighterFactory fighterFactory, Fight fight) {
        super(fighterFactory, fight);

        this.monsterService = monsterService;
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        handle(cast, effect);
    }

    @Override
    protected PlayableFighter createInvocation(int id, Fighter invoker, SpellEffect effect) {
        return new InvocationFighter(
            id,
            monsterService.load(effect.min()).get(effect.max()),
            invoker.team(),
            invoker
        );
    }

    @Override
    protected ActionEffect createPacket(Fighter invoker, PlayableFighter invocation) {
        return ActionEffect.addInvocation(invoker, invocation);
    }
}
