package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate spell constraints
 */
final public class SpellConstraintsValidator {
    final private FightTurn turn;
    final private SpellConstraintValidator[] validators;

    public SpellConstraintsValidator(FightTurn turn) {
        this(turn, new SpellConstraintValidator[] {
            new ApCostValidator(),
            new TargetCellValidator(),
            new LineLaunchValidator()
        });
    }

    public SpellConstraintsValidator(FightTurn turn, SpellConstraintValidator[] validators) {
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
        for (SpellConstraintValidator validator : validators) {
            Error error = validator.validate(turn, spell, target);

            if (error != null) {
                return error;
            }
        }

        return null;
    }
}
