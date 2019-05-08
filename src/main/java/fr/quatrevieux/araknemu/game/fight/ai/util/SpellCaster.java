package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastFactory;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Utility class for cast spells
 */
final public class SpellCaster {
    final private FightTurn turn;
    final private CastConstraintValidator<Spell> validator;
    final private CastFactory factory;

    public SpellCaster(AI ai) {
        turn = ai.turn();
        factory = turn.actions().cast();
        validator = factory.validator();
    }

    /**
     * Validate the cast action
     */
    public boolean validate(Spell spell, FightCell target) {
        return validator.validate(turn, spell, target) == null;
    }

    /**
     * Create the action
     */
    public Cast create(Spell spell, FightCell target) {
        return factory.create(spell, target);
    }
}
