package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.castable.validator.*;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate spell constraints
 */
final public class SpellConstraintsValidator implements CastConstraintValidator<Spell> {
    final private CastConstraintValidator<Spell> validator;

    public SpellConstraintsValidator() {
        this(new CastConstraintValidator[] {
            new ApCostValidator(),
            new TargetCellValidator(),
            new LineLaunchValidator(),
            new StatesValidator(),
            new RangeValidator(),
            new SpellLaunchValidator()
        });
    }

    public SpellConstraintsValidator(CastConstraintValidator<? super Spell>[] validators) {
        this.validator = new ConstraintsAggregateValidator<>(validators);
    }

    @Override
    public Error validate(FightTurn turn, Spell spell, FightCell target) {
        return validator.validate(turn, spell, target);
    }
}
