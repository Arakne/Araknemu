package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.PathStep;
import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFailed;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;

final public class TackleValidator implements PathValidatorFight {
    @Override
    public MoveResult validate(Move move, MoveResult result) {
        BattlefieldMap map = move.performer().cell().map();
        PathStep<FightCell> step = result.path().first();

        FightCell[] cells = new FightCell[]{
            map.get(Direction.NORTH_EAST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
            map.get(Direction.NORTH_WEST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
            map.get(Direction.SOUTH_EAST.nextCellIncrement(map.dimensions().width()) + step.cell().id()),
            map.get(Direction.SOUTH_WEST.nextCellIncrement(map.dimensions().width()) + step.cell().id())
        };

        for (FightCell fightCell : cells) {

            if(!fightCell.fighter().isPresent()) {
                continue;
            }

            if(fightCell.fighter().isPresent() && fightCell.fighter().get().team().equals(move.performer().team())) {
                continue;
            }

            int chance = getTackle(move.performer(), fightCell.fighter().get());
            int random = RandomUtil.createShared().rand(0, 100);

            if( random > chance) {
                int lostPa = (int)(move.performer().fight().turnList().current().get().points().actionPoints() * (chance / 100d));                   
                return new MoveFailed(move.performer(), lostPa);
            }
        }

        return result;
    }

    private int getTackle(Fighter fighter, PassiveFighter toTackle) {
        int fighterAgility = fighter.characteristics().get(Characteristic.AGILITY);
        int toTackleAgility = toTackle.characteristics().get(Characteristic.AGILITY);

        int a = fighterAgility + 25;
        int b = toTackleAgility + fighterAgility + 50;

        if (b <= 0) {
            b = 1;
        }

        int chance = (int) ((300 * a / b) - 100);
		if (chance < 10)
			chance = 10;
		if (chance > 90)
            chance = 90;

		return chance;
    }
}
