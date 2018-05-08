package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate the target cell
 */
final public class TargetCellValidator implements SpellConstraintValidator {
    @Override
    public Error validate(FightTurn turn, Spell spell, FightCell target) {
        if (!target.walkableIgnoreFighter()) {
            return Error.cantCastCellNotAvailable();
        }

        if (spell.constraints().freeCell() && target.fighter().isPresent()) {
            return Error.cantCastInvalidCell();
        }

        return null;
    }
}
