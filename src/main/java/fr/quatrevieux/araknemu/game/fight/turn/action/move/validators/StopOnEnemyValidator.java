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
        final BattlefieldMap map = move.performer().cell().map();

        // @todo do not check the last step
        for (int i = 1; i < result.path().size(); i++) {
            final PathStep<FightCell> step = result.path().get(i);

            for (Direction direction : Direction.restrictedDirections()) {
                // @todo use Decoder
                final int adjacentCellId = direction.nextCellIncrement(map.dimensions().width()) + step.cell().id();

                if (map.size() <= adjacentCellId) {
                    continue;
                }

                final FightCell fightCell = map.get(adjacentCellId);

                if (fightCell.fighter().isPresent() && !fightCell.fighter().get().team().equals(move.performer().team())) {
                    // Truncate the path until the current cell (index + 1 because the argument is a size)
                    return new MoveSuccess(move.performer(), result.path().truncate(i + 1));
                }
            }
        }

        return result;
    }
    
}
