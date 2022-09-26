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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Effect for steal action points
 *
 * Like {@link ActionPointLostHandler} the removal can be dodged
 * This effect is equivalent to apply {@link ActionPointLostHandler} chained with {@link fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddActionPointsHandler}
 *
 * In case of direct effect (i.e. without duration), action points will simply be added to the current turn
 */
public final class StealActionPointHandler extends AbstractStealPointHandler {
    /**
     * @param fight Fight where the effect will be applied
     * @param removeActionPointEffect Effect id used by the "remove action point" buff
     * @param addActionPointEffect Effect id used by the "add action point" buff
     */
    public StealActionPointHandler(Fight fight, int removeActionPointEffect, int addActionPointEffect) {
        super(
            fight,
            new ActionPointLostApplier(fight, removeActionPointEffect),
            AlterPointHook.addActionPoint(fight),
            addActionPointEffect
        );
    }

    @Override
    protected void applyOnCurrentTurn(Fight fight, Turn turn, FighterData caster, @Positive int toAdd) {
        turn.points().addActionPoints(toAdd);
        fight.send(ActionEffect.addActionPoints(caster, toAdd));
    }
}
