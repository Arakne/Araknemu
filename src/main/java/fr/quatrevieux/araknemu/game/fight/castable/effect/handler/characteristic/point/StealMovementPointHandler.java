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
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Effect for steal movement points
 *
 * Like {@link MovementPointLostHandler} the removal can be dodged
 * This effect is equivalent to apply {@link MovementPointLostHandler} chained with {@link fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddMovementPointsHandler}
 *
 * In case of direct effect (i.e. without duration), movement points will simply be added to the current turn
 */
public final class StealMovementPointHandler extends AbstractStealPointHandler {
    /**
     * @param fight Fight where the effect will be applied
     * @param removeMovementPointEffect Effect id used by the "remove movement point" buff
     * @param addMovementPointEffect Effect id used by the "add movement point" buff
     */
    public StealMovementPointHandler(Fight fight, int removeMovementPointEffect, int addMovementPointEffect) {
        super(
            fight,
            new MovementPointLostApplier(fight, removeMovementPointEffect),
            AlterPointHook.addMovementPoint(fight),
            addMovementPointEffect
        );
    }

    @Override
    protected void applyOnCurrentTurn(Fight fight, Turn turn, ActiveFighter caster, @Positive int toAdd) {
        turn.points().addMovementPoints(toAdd);
        fight.send(ActionEffect.addMovementPoints(caster, toAdd));
    }
}
