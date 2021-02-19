package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidationException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFailed;

final public class TackleValidator implements PathValidatorFight {
    @Override
    public Path<FightCell> validate(Move move, Path<FightCell> path) throws PathValidationException {
        int currentStep = 0;
        Iterator<PathStep<FightCell>> iterator = path.iterator();
        BattlefieldMap map = move.performer().cell().map();

        while (iterator.hasNext()) {
            currentStep++;
            PathStep<FightCell> step = iterator.next();

            List<FightCell> cells = Arrays.asList(
                new FightCell[]{
                    map.get(Direction.NORTH_EAST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
                    map.get(Direction.NORTH_WEST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
                    map.get(Direction.SOUTH_EAST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
                    map.get(Direction.SOUTH_WEST.nextCellIncrement(map.dimensions().width()) + step.cell().id())
                }
            );

            for (FightCell fightCell : cells) {
                if (currentStep == path.size()) {
                    continue; // don't check the final step
                }

                if(!fightCell.fighter().isPresent()) {
                    continue;
                }

                if(fightCell.fighter().isPresent() && fightCell.fighter().get().team().equals(move.performer().team())) {
                    continue;
                }

                int esquive = getTackle(move.performer(), fightCell.fighter().get());
                int random = RandomUtils.nextInt(0, 101);

                if( random > esquive) {
                    int lostPa = (int)(move.performer().fight().turnList().current().get().points().actionPoints() * (esquive / 100d));                   
                    iterator.forEachRemaining(action -> iterator.remove());

                    throw new PathValidationException(new MoveFailed(move.performer(), path, lostPa));
                }
            }
        }

        return path;
    }

    private int getTackle(Fighter fighter, PassiveFighter toTackle) {
        int fighterAgility = fighter.characteristics().get(Characteristic.AGILITY);
        int toTackleAgility = toTackle.characteristics().get(Characteristic.AGILITY);

        int a = fighterAgility + 25;
        int b = toTackleAgility + fighterAgility + 50;

        if (b <= 0) {
            b = 1;
        }

        int chan = (int) ((long) (300 * a / b) - 100);
		if (chan < 10)
			chan = 10;
		if (chan > 90)
            chan = 90;

		return chan;
    }
}
