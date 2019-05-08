package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Factory for cast action
 */
final public class CastFactory implements FightActionFactory {
    final private FightTurn turn;
    final private Fighter fighter;
    final private CastConstraintValidator<Spell> validator;
    final private CriticalityStrategy criticalityStrategy;

    public CastFactory(FightTurn turn) {
        this(turn, new SpellConstraintsValidator(), new BaseCriticalityStrategy(turn.fighter()));
    }

    public CastFactory(FightTurn turn, CastConstraintValidator<Spell> validator, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.fighter = turn.fighter();
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public Action create(String[] arguments) {
        final int spellId = Integer.parseInt(arguments[0]);

        return create(
            fighter.spells().has(spellId) ? fighter.spells().get(spellId) : null,
            turn.fight().map().get(Integer.parseInt(arguments[1]))
        );
    }

    @Override
    public ActionType type() {
        return ActionType.CAST;
    }

    /**
     * Create the cast action
     *
     * @param spell The spell to cast
     * @param target The cell target
     */
    public Cast create(Spell spell, FightCell target) {
        return new Cast(turn, fighter, spell, target, validator, criticalityStrategy);
    }

    /**
     * Get the spell cast validator
     */
    public CastConstraintValidator<Spell> validator() {
        return validator;
    }
}
