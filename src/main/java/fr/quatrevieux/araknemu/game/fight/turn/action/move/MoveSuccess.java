package fr.quatrevieux.araknemu.game.fight.turn.action.move;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.world.map.path.Path;

/**
 * Successful move result
 */
final class MoveSuccess implements ActionResult {
    final private Fighter performer;
    final private Path<FightCell> path;

    public MoveSuccess(Fighter performer, Path<FightCell> path) {
        this.performer = performer;
        this.path = path;
    }

    @Override
    public int action() {
        return ActionType.MOVE.id();
    }

    @Override
    public Fighter performer() {
        return performer;
    }

    @Override
    public Object[] arguments() {
        return new Object[] { path.encode() };
    }

    @Override
    public boolean success() {
        return true;
    }

    public FightCell target() {
        return path.target();
    }

    public int steps() {
        return path.size() - 1; // The path contains the current fighter's cell
    }
}
