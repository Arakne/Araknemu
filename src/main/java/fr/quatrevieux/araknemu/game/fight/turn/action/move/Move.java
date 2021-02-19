/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.move;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFailed;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveSuccess;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;

/**
 * Move the fighter
 */
final public class Move implements Action {
    final private FightTurn turn;
    final private Fighter fighter;
    final private Path<FightCell> path;

    private MoveStatus result;

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

    private MoveStatus handleTackle() {
        int currentStep = 0;
        Iterator<PathStep<FightCell>> iterator = path.iterator();
        BattlefieldMap map = fighter.cell().map();

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

                if(fightCell.fighter().isPresent() && fightCell.fighter().get().team().equals(fighter.team())) {
                    continue;
                }

                int esquive = getTackle(fighter, fightCell.fighter().get());
                int random = RandomUtils.nextInt(0, 101);

                if( random > esquive) {
                    int lostPa = (int)(turn.points().actionPoints() * (esquive / 100d));                   
                    iterator.forEachRemaining(action -> iterator.remove());

                    return new MoveFailed(fighter, path, lostPa);
                }
            }
        }

        return new MoveSuccess(fighter, path);
    }

    @Override
    public ActionResult start() {
        result = handleTackle();

        return result;
    }

    @Override
    public void end() {
        turn.points().useMovementPoints(result.steps());
        fighter.move(result.target());
        fighter.setOrientation(result.orientation());
    }

    @Override
    public void failed() {

        turn.points().useActionPoints(result.lostActionPoints());
        turn.points().useMovementPoints(turn.points().movementPoints());

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
        // @todo handle walk and run
        return Duration.ofMillis(300L * path.size());
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
