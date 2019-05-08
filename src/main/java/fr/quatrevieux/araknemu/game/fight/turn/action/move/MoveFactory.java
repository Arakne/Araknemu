package fr.quatrevieux.araknemu.game.fight.turn.action.move;

import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionFactory;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;

/**
 * Factory for move action
 */
final public class MoveFactory implements FightActionFactory {
    final private FightTurn turn;

    public MoveFactory(FightTurn turn) {
        this.turn = turn;
    }

    @Override
    public Action create(String[] arguments) {
        return create(new Decoder<>(turn.fight().map()).decode(arguments[0], turn.fighter().cell()));
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    /**
     * Create the move action
     */
    public Move create(Path<FightCell> path) {
        return new Move(turn, turn.fighter(), path);
    }
}
