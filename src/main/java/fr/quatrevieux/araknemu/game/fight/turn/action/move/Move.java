package fr.quatrevieux.araknemu.game.fight.turn.action.move;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.world.map.path.Path;

import java.time.Duration;

/**
 * Move the fighter
 */
final public class Move implements Action {
    final private FightTurn turn;
    final private Fighter fighter;
    final private Path<FightCell> path;

    private MoveSuccess result;

    public Move(FightTurn turn, Fighter fighter, Path<FightCell> path) {
        this.turn = turn;
        this.fighter = fighter;
        this.path = path;
    }

    @Override
    public boolean validate() {
        return
            path.size() > 1
            && turn.points().movementPoints() >= path.size() - 1
            && path.stream().skip(1).allMatch(step -> step.cell().walkable() && step.direction().restricted())
        ;
    }

    @Override
    public ActionResult start() {
        result = new MoveSuccess(fighter, path);

        return result;
    }

    @Override
    public void end() {
        turn.points().useMovementPoints(result.steps());
        fighter.move(result.target());
    }

    @Override
    public Fighter performer() {
        return fighter;
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public Duration duration() {
        return Duration.ofMillis(300 * path.size());
    }
}
