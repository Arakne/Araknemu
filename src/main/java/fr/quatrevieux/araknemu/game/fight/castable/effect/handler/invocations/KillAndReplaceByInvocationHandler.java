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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.BaseCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;

/**
 * Kill targets and replace them by invocation
 *
 * A new fighter will be created and added to fight and timeline (turn list)
 * If the caster has reached the invocation limit, this effect will only kill the targets
 *
 * Effect parameters :
 * - #1 (min) : monster id
 * - #2 (max) : grade number
 *
 * @see InvocationFighter Invoked fighter
 */
public final class KillAndReplaceByInvocationHandler implements EffectHandler {
    private final MonsterService monsterService;
    private final FighterFactory fighterFactory;
    private final Fight fight;
    private final InvocationCountValidator validator;

    public KillAndReplaceByInvocationHandler(MonsterService monsterService, FighterFactory fighterFactory, Fight fight) {
        this.monsterService = monsterService;
        this.fighterFactory = fighterFactory;
        this.fight = fight;
        this.validator = new InvocationCountValidator(fight);
    }

    @Override
    public void handle(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        final Fighter caster = cast.caster();

        for (Fighter target : effect.targets()) {
            if (!fight.active()) {
                break;
            }

            final FightCell cell = target.cell();

            target.life().kill(caster);

            if (fight.active() && validator.check(caster)) {
                addInvocation(caster, effect.effect(), cell);
            }
        }
    }

    @Override
    public void buff(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use KillAndReplaceByInvocation as a buff");
    }

    private void addInvocation(Fighter caster, SpellEffect effect, FightCell target) {
        final PlayableFighter invocation = fighterFactory.generate(id -> new InvocationFighter(
            id,
            monsterService.load(effect.min()).get(effect.max()),
            caster.team(),
            caster
        ));

        fight.fighters().joinTurnList(invocation, target);

        invocation.init();

        fight.send(ActionEffect.addInvocation(caster, invocation));
        fight.send(ActionEffect.packet(caster, new FighterTurnOrder(fight.turnList())));
    }
}
