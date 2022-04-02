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
 * Copyright (c) 2017-2021 Vincent Quatrevieux Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveSuccess;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StopOnEnemyValidatorTest extends FightBaseCase {
    private Fight fight;
    private FightTurn turn;
    private Fighter fighter;
    private StopOnEnemyValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        validator = new StopOnEnemyValidator();
        fight = createFight();
        fighter = player.fighter();
        fighter.move(fight.map().get(185));
        turn = new FightTurn(player.fighter(), fight, Duration.ofSeconds(30));
        turn.start();
    }

    @Test
    void validatePathWithNoEnnemy() {
        Path<FightCell> path = new Path<FightCell>(
            new Decoder<FightCell>(fight.map()),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(198), Direction.NORTH_WEST)
            )
        );
        
        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);
        
        MoveResult result = new MoveSuccess(fighter, path);

        MoveResult result2 = validator.validate(move, result);

        assertSame(result, result2);
    }

    @Test
    void validatePathWithEnnemy() throws SQLException {
        Path<FightCell> path = new Path<FightCell>(
            new Decoder<FightCell>(fight.map()),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        other.fighter().move(fight.map().get(198));
        
        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = new MoveSuccess(fighter, path);

        MoveResult result2 = validator.validate(move, result);

        assertNotSame(result, result2);
        assertEquals(4, result.path().size());
        assertEquals(3, result2.path().size());
        assertEquals(2, result2.lostMovementPoints());
        assertEquals(213, result2.target().id());
    }

    @Test
    void validatePathWithEnnemyOnLastStepShouldDoNothing() throws SQLException {
        Path<FightCell> path = new Path<FightCell>(
            new Decoder<FightCell>(fight.map()),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        other.fighter().move(fight.map().get(212));

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = new MoveSuccess(fighter, path);

        MoveResult result2 = validator.validate(move, result);

        assertSame(result, result2);
    }

    @Test
    void functionalStopped() {
        other.fighter().move(fight.map().get(198));

        Move move = turn.actions().move().create(new Path<FightCell>(
            new Decoder<FightCell>(fight.map()),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        ));

        fight.start();

        turn.perform(move);
        turn.terminate();

        assertEquals(213, fighter.cell().id());
        assertEquals(1, fighter.turn().points().movementPoints());
        assertEquals(6, fighter.turn().points().actionPoints());
    }
}
