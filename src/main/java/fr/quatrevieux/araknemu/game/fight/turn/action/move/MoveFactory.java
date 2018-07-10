package fr.quatrevieux.araknemu.game.fight.turn.action.move;

import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionFactory;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;

/**
 * Factory for move action
 */
final public class MoveFactory implements FightActionFactory {
    final private FightTurn turn;

    public MoveFactory(FightTurn turn) {
        this.turn = turn;
    }

    @Override
    public Action create(ActionType action, String[] arguments) throws Exception {
        return new Move(
            turn,
            turn.fighter(),
            new Decoder<>(turn.fight().map()).decode(
                arguments[0],
                turn.fighter().cell()
            )
        );
    }
}
