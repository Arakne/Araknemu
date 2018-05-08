package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate spell AP cost
 */
final public class ApCostValidator implements SpellConstraintValidator {
    @Override
    public Error validate(FightTurn turn, Spell spell, FightCell target) {
        return spell.apCost() > turn.points().actionPoints()
            ? Error.cantCastNotEnoughActionPoints(turn.points().actionPoints(), spell.apCost())
            : null
        ;
    }
}
