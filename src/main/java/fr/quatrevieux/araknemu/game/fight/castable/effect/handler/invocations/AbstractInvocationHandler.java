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
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Base effect handler for create invocation
 * This handler will handle invocation count limit validation, create the invocation and add it to the turn list
 *
 * The implementation should provide :
 * - The invocation fighter instantiation
 * - The related ActionEffect packet
 *
 * Note: this handler is only used for playable invocation, not static ones
 */
abstract class AbstractInvocationHandler implements EffectHandler {
    private final FighterFactory fighterFactory;
    private final Fight fight;
    private final InvocationCountValidator validator;

    public AbstractInvocationHandler(FighterFactory fighterFactory, Fight fight) {
        this.fighterFactory = fighterFactory;
        this.fight = fight;
        this.validator = new InvocationCountValidator(fight);
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Invocations cannot be used as buff");
    }

    @Override
    public final void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter caster = cast.caster();

        final PlayableFighter invocation = fighterFactory.generate(id -> createInvocation(id, caster, effect.effect()));

        fight.fighters().joinTurnList(invocation, cast.target());

        invocation.init();

        fight.send(createPacket(caster, invocation));
        fight.send(ActionEffect.packet(cast.caster(), new FighterTurnOrder(fight.turnList())));
    }

    protected abstract PlayableFighter createInvocation(int id, Fighter invoker, SpellEffect effect);

    protected abstract ActionEffect createPacket(Fighter invoker, PlayableFighter invocation);

    @Override
    public final boolean check(Turn turn, Castable castable, BattlefieldCell target) {
        return validator.check(turn, castable, target);
    }

    @Override
    public final @Nullable Error validate(Turn turn, Castable castable, BattlefieldCell target) {
        return validator.validate(turn, castable, target);
    }
}
