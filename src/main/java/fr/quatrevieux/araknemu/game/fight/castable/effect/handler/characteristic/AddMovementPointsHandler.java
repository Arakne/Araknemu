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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point.AlterPointHook;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Buff effect for adding movement points
 * If this effect is not used as buff, it will add movement points to the current turn
 */
public final class AddMovementPointsHandler extends AbstractAlterCharacteristicHandler {
    private final Fight fight;

    public AddMovementPointsHandler(Fight fight) {
        super(AlterPointHook.addMovementPoint(fight), true);

        this.fight = fight;
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        fight.turnList().current().ifPresent(turn -> {
            final ActiveFighter fighter = turn.fighter();
            final EffectValue value = EffectValue.create(effect.effect(), fighter, fighter);
            final int mp = value.value();

            turn.points().addMovementPoints(mp);
            fight.send(ActionEffect.addMovementPoints(fighter, mp));
        });
    }
}
