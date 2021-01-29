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

package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.castable.validator.ApCostValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.ConstraintsAggregateValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.LineLaunchValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.LineOfSightValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.RangeValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.SpellLaunchValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.StatesValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.TargetCellValidator;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate spell constraints
 */
final public class SpellConstraintsValidator implements CastConstraintValidator<Spell> {
    final private CastConstraintValidator<Spell> validator;

    @SuppressWarnings("unchecked")
    public SpellConstraintsValidator() {
        this(new CastConstraintValidator[] {
            new ApCostValidator(),
            new TargetCellValidator(),
            new LineLaunchValidator(),
            new StatesValidator(),
            new RangeValidator(),
            new SpellLaunchValidator(),
            new LineOfSightValidator(),
        });
    }

    public SpellConstraintsValidator(CastConstraintValidator<? super Spell>[] validators) {
        this.validator = new ConstraintsAggregateValidator<>(validators);
    }

    @Override
    public Error validate(Turn turn, Spell spell, FightCell target) {
        return validator.validate(turn, spell, target);
    }
}
