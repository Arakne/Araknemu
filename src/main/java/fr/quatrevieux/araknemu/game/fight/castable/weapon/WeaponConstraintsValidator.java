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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.quatrevieux.araknemu.game.fight.castable.validator.ApCostValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.ConstraintsAggregateValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.LineOfSightValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.RangeValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.StatesValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.TargetCellValidator;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate weapon cast constraints
 */
final public class WeaponConstraintsValidator implements CastConstraintValidator<CastableWeapon> {
    final private CastConstraintValidator<CastableWeapon> validator;

    @SuppressWarnings("unchecked")
    public WeaponConstraintsValidator() {
        this(new CastConstraintValidator[] {
            new ApCostValidator(),
            new TargetCellValidator(),
            new StatesValidator(),
            new RangeValidator(),
            new LineOfSightValidator(),
        });
    }

    public WeaponConstraintsValidator(CastConstraintValidator<? super CastableWeapon>[] validators) {
        this.validator = new ConstraintsAggregateValidator<>(validators);
    }

    @Override
    public Error validate(Turn turn, CastableWeapon weapon, FightCell target) {
        return validator.validate(turn, weapon, target);
    }
}
