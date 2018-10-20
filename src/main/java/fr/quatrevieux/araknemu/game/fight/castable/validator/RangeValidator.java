package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate the cast range
 */
final public class RangeValidator implements CastConstraintValidator {
    @Override
    public Error validate(FightTurn turn, Castable castable, FightCell target) {
        CoordinateCell<FightCell> from = new CoordinateCell<>(turn.fighter().cell());
        CoordinateCell<FightCell> to   = new CoordinateCell<>(target);

        int distance = from.distance(to);

        Interval range = castable.constraints().range();

        if (castable.modifiableRange()) {
            range = range.modify(turn.fighter().characteristics().get(Characteristic.SIGHT_BOOST));
        }

        if (!range.contains(distance)) {
            return Error.cantCastBadRange(range, distance);
        }

        return null;
    }
}
