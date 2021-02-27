package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import java.util.Iterator;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;

final public class StopOnEnemyValidator implements PathValidatorFight {

    @Override
    public MoveResult validate(Move move, MoveResult result) {
        Iterator<PathStep<FightCell>> iterator = result.path().iterator();
        BattlefieldMap map = move.performer().cell().map();

        while (iterator.hasNext()) {
            PathStep<FightCell> step = iterator.next();

            FightCell[] cells = new FightCell[]{
                map.get(Direction.NORTH_EAST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
                map.get(Direction.NORTH_WEST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
                map.get(Direction.SOUTH_EAST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
                map.get(Direction.SOUTH_WEST.nextCellIncrement(map.dimensions().width()) + step.cell().id())
            };

            for (FightCell fightCell : cells) {
                if(fightCell.fighter().isPresent() && !fightCell.fighter().get().team().equals(move.performer().team())) {
                    iterator.forEachRemaining(action -> iterator.remove());
                }
            }
        }

        return result;
    }
    
}
