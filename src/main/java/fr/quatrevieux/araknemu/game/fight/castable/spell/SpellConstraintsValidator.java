package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.castable.validator.*;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate spell constraints
 */
final public class SpellConstraintsValidator {
    final private FightTurn turn;
    final private CastConstraintValidator<? super Spell>[] validators;

    public SpellConstraintsValidator(FightTurn turn) {
        this(turn, new CastConstraintValidator[] {
            new ApCostValidator(),
            new TargetCellValidator(),
            new LineLaunchValidator(),
            new StatesValidator(),
            new RangeValidator()
        });
    }

    public SpellConstraintsValidator(FightTurn turn, CastConstraintValidator<? super Spell>[] validators) {
        this.turn = turn;
        this.validators = validators;
    }

    /**
     * Validate the spell cast
     *
     * @param spell Spell to cast
     * @param target The target cell
     *
     * @return The error if cannot cast the spell, or null on success
     */
    public Error validate(Spell spell, FightCell target) {
        for (CastConstraintValidator<? super Spell> validator : validators) {
            Error error = validator.validate(turn, spell, target);

            if (error != null) {
                return error;
            }
        }

        return null;
    }
}
