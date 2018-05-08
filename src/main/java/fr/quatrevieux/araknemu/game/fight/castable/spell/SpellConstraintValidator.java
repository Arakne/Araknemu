package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate a single spell constraint
 */
public interface SpellConstraintValidator {
    /**
     * Check if the spell can be casted
     *
     * @param turn The current turn
     * @param spell The spell to cast
     * @param target The target cell
     *
     * @return The error if constraint failed, or null
     */
    public Error validate(FightTurn turn, Spell spell, FightCell target);
}
