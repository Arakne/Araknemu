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
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;

/**
 * Create a double of the caster
 * This spell effect does not take any parameters
 *
 * @see DoubleFighter
 */
public final class CreateDoubleHandler implements EffectHandler {
     private final FighterFactory fighterFactory;
     private final Fight fight;

    public CreateDoubleHandler(FighterFactory fighterFactory, Fight fight) {
        this.fighterFactory = fighterFactory;
        this.fight = fight;
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("CreateDoubleHandler cannot be used as buff");
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter caster = cast.caster();

        final PlayableFighter invocation = fighterFactory.generate(id -> new DoubleFighter(
            id,
            cast.caster()
        ));

        fight.fighters().joinTurnList(invocation, cast.target());

        invocation.init();

        fight.send(ActionEffect.addDouble(caster, invocation));
        fight.send(ActionEffect.packet(cast.caster(), new FighterTurnOrder(fight.turnList())));
    }
}
