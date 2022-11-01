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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.module.RaulebaqueModule;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;

/**
 * Handle the Raulebaque effect
 */
public final class RaulebaqueHandler implements EffectHandler {
    private final Fight fight;
    private final RaulebaqueModule module;

    public RaulebaqueHandler(Fight fight, RaulebaqueModule module) {
        this.fight = fight;
        this.module = module;
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        module.startPositions().forEach((fighter, startCell) -> {
            if (fighter.dead()) {
                return;
            }

            final FightCell lastCell = fighter.cell();

            if (lastCell.equals(startCell)) {
                return;
            }

            fighter.move(null);

            final Fighter other = startCell.fighter();

            // Cell is not free : exchange place
            if (other != null) {
                other.move(lastCell);
            }

            fighter.move(startCell);
        });

        fight.send(new FighterPositions(fight.fighters()));
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use Raulebaque as buff effect");
    }
}
