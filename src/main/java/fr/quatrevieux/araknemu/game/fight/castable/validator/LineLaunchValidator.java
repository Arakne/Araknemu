package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Check if spell should be cast in line launch
 */
final public class LineLaunchValidator implements CastConstraintValidator {
    @Override
    public Error validate(FightTurn turn, Castable castable, FightCell target) {
        if (!castable.constraints().lineLaunch()) {
            return null;
        }

        if (castable.constraints().range().max() < 2 || target.equals(turn.fighter().cell())) {
            return null;
        }

        final int mapWidth = turn.fight().map().dimensions().width();

        for (Direction direction : Direction.values()) {
            if (!direction.restricted()) {
                continue;
            }

            int diff = Math.abs(turn.fighter().cell().id() - target.id());
            int inc  = direction.nextCellIncrement(mapWidth);

            // cell ids diff is multiple of inc => cells are in same line
            if (diff / inc < (mapWidth * 2 + 1) && diff % inc == 0) {
                return null;
            }
        }

        return Error.cantCastLineLaunch();
    }
}
