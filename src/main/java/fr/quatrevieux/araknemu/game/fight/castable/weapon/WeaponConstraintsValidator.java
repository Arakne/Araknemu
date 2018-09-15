package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.quatrevieux.araknemu.game.fight.castable.validator.*;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate weapon cast constraints
 */
final public class WeaponConstraintsValidator {
    final private FightTurn turn;
    final private CastConstraintValidator<? super CastableWeapon>[] validators;

    public WeaponConstraintsValidator(FightTurn turn) {
        this(turn, new CastConstraintValidator[] {
            new ApCostValidator(),
            new TargetCellValidator(),
            new StatesValidator()
        });
    }

    public WeaponConstraintsValidator(FightTurn turn, CastConstraintValidator<? super CastableWeapon>[] validators) {
        this.turn = turn;
        this.validators = validators;
    }

    /**
     * Validate the weapon cast
     *
     * @param weapon Weapon to cast
     * @param target The target cell
     *
     * @return The error if cannot cast the weapon, or null on success
     */
    public Error validate(CastableWeapon weapon, FightCell target) {
        for (CastConstraintValidator<? super CastableWeapon> validator : validators) {
            Error error = validator.validate(turn, weapon, target);

            if (error != null) {
                return error;
            }
        }

        return null;
    }
}
