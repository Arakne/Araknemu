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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

/**
 * Move the adjacent fighter to the target cell
 *
 * - Get the direction between caster and target cell
 * - Get the adjacent fighter for the given direction
 * - Compute the distance between the target fighter and the target cell
 * - Perform a move back on the target fighter with the computed distance
 */
public final class MoveToTargetCellHandler implements EffectHandler {
    private final Decoder<FightCell> decoder;
    private final MoveBackApplier applier;

    public MoveToTargetCellHandler(Fight fight) {
        this.decoder = fight.map().decoder();
        this.applier = new MoveBackApplier(fight);
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter caster = cast.caster();
        final FightCell casterCell = cast.from();
        final CoordinateCell<BattlefieldCell> casterCellCoordinate = casterCell.coordinate();

        // Remove 1 because the distance should be computed from the target fighter cell
        final int distance = casterCellCoordinate.distance(cast.target()) - 1;
        final Direction direction = casterCellCoordinate.directionTo(cast.target());

        if (distance < 1) {
            return;
        }

        decoder.nextCellByDirection(casterCell, direction)
            .map(FightCell::fighter)
            .ifPresent(target -> applier.apply(caster, cast.from(), target, distance))
        ;
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use move back as buff effect");
    }
}
