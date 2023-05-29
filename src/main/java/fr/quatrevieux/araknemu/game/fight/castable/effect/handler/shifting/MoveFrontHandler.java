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

import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

import java.util.Optional;

/**
 * Move front target
 *
 * Unlike move back, this effect do not handle area effects, and do not apply any damage when blocked
 */
public final class MoveFrontHandler implements EffectHandler {
    private final Fight fight;
    private final Decoder<FightCell> decoder;

    public MoveFrontHandler(Fight fight) {
        this.fight = fight;
        this.decoder = fight.map().decoder();
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final Fighter caster = cast.caster();

        for (Fighter target : effect.targets()) {
            apply(caster, cast.from(), target, effect.effect().min());
        }
    }

    private void apply(FighterData caster, FightCell from, Fighter target, int distance) {
        final FightCell startCell = target.cell();
        final Direction direction = startCell.coordinate().directionTo(from);

        FightCell destination = startCell;

        // Check if a cell block the movement
        for (int i = 0; i < distance; ++i) {
            final Optional<FightCell> nextCell = decoder
                .nextCellByDirection(destination, direction)
                .filter(MapCell::walkable)
            ;

            if (!nextCell.isPresent()) {
                break;
            }

            destination = nextCell.get();
        }

        // Fighter has moved
        if (!destination.equals(startCell)) {
            fight.send(ActionEffect.slide(caster, target, destination));
            target.move(destination);
        }
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use move back as buff effect");
    }
}
