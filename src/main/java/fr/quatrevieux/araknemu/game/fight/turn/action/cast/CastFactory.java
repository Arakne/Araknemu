package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;

/**
 * Factory for cast action
 */
final public class CastFactory implements FightActionFactory {
    final private FightTurn turn;
    final private SpellConstraintsValidator validator;
    final private CriticalityStrategy criticalityStrategy;

    public CastFactory(FightTurn turn) {
        this(turn, new SpellConstraintsValidator(turn), new BaseCriticalityStrategy(turn.fighter()));
    }

    public CastFactory(FightTurn turn, SpellConstraintsValidator validator, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public Action create(ActionType action, String[] arguments) {
        Fighter fighter = turn.fighter();
        int spellId = Integer.parseInt(arguments[0]);

        return new Cast(
            turn,
            fighter,
            fighter.spells().has(spellId) ? fighter.spells().get(spellId) : null,
            turn.fight().map().get(Integer.parseInt(arguments[1])),
            validator,
            criticalityStrategy
        );
    }
}
