package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveSuccess;

final public class StopOnEnemyValidator implements PathValidatorFight {

    @Override
    public MoveResult validate(Move move, MoveResult result) {
        BattlefieldMap map = move.performer().cell().map();

        for (int i = 0; i < result.path().size(); i++) {
            PathStep<FightCell> step = result.path().get(i);

            if (step.equals(result.path().first()))
                continue;

            FightCell[] cells = new FightCell[]{
                map.get(Direction.NORTH_EAST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
                map.get(Direction.NORTH_WEST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
                map.get(Direction.SOUTH_EAST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
                map.get(Direction.SOUTH_WEST.nextCellIncrement(map.dimensions().width()) + step.cell().id())
            };

            for (FightCell fightCell : cells) {
                if(fightCell.fighter().isPresent() && !fightCell.fighter().get().team().equals(move.performer().team())) {
                    return new MoveSuccess(move.performer(), result.path().truncate(i));
                }
            }
        }

        return result;
    }
    
}
