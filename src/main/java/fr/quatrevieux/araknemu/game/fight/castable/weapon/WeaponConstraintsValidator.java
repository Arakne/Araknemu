package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.quatrevieux.araknemu.game.fight.castable.validator.*;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate weapon cast constraints
 */
final public class WeaponConstraintsValidator implements CastConstraintValidator<CastableWeapon> {
    final private CastConstraintValidator<CastableWeapon> validator;

    public WeaponConstraintsValidator() {
        this(new CastConstraintValidator[] {
            new ApCostValidator(),
            new TargetCellValidator(),
            new StatesValidator(),
            new RangeValidator()
        });
    }

    public WeaponConstraintsValidator(CastConstraintValidator<? super CastableWeapon>[] validators) {
        this.validator = new ConstraintsAggregateValidator<>(validators);
    }

    @Override
    public Error validate(FightTurn turn, CastableWeapon weapon, FightCell target) {
        return validator.validate(turn, weapon, target);
    }
}
