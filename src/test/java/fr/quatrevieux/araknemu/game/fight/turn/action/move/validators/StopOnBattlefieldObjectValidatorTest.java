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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveSuccess;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class StopOnBattlefieldObjectValidatorTest extends FightBaseCase {
    private Fight fight;
    private FightTurn turn;
    private PlayableFighter fighter;
    private StopOnBattlefieldObjectValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        validator = new StopOnBattlefieldObjectValidator();
        fight = createFight();
        fighter = player.fighter();
        fighter.move(fight.map().get(185));
        other.fighter().move(fight.map().get(123));
        turn = new FightTurn(player.fighter(), fight, Duration.ofSeconds(30));
        turn.start();
    }

    @Test
    void validatePathWithNoObjects() {
        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
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
    void validatePathWithNoBlockingObjects() {
        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.shouldStopMovement()).thenReturn(false);
        Mockito.when(bo.cell()).thenReturn(fight.map().get(199));
        Mockito.when(bo.size()).thenReturn(3);
        fight.map().objects().add(bo);

        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
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
    void validatePathWithBlockingObject() throws SQLException {
        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.shouldStopMovement()).thenReturn(true);
        Mockito.when(bo.cell()).thenReturn(fight.map().get(183));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        fight.map().objects().add(bo);

        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

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
    void validatePathWithObjectOnLastStepShouldDoNothing() throws SQLException {
        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.shouldStopMovement()).thenReturn(true);
        Mockito.when(bo.cell()).thenReturn(fight.map().get(197));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        fight.map().objects().add(bo);

        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = new MoveSuccess(fighter, path);

        MoveResult result2 = validator.validate(move, result);

        assertSame(result, result2);
    }

    @Test
    void validatePathAlreadyOnAreaShouldStopOnFirstStep() throws SQLException {
        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.shouldStopMovement()).thenReturn(true);
        Mockito.when(bo.cell()).thenReturn(fight.map().get(185));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        fight.map().objects().add(bo);

        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = new MoveSuccess(fighter, path);
        MoveResult result2 = validator.validate(move, result);

        assertNotSame(result, result2);
        assertEquals(2, result2.path().size());
        assertEquals(1, result2.lostMovementPoints());
        assertEquals(199, result2.target().id());
    }

    @Test
    void functionalStopped() {
        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.shouldStopMovement()).thenReturn(true);
        Mockito.when(bo.cell()).thenReturn(fight.map().get(183));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        fight.map().objects().add(bo);

        Move move = (Move) fight.actions().move().create(fighter, new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        ));

        fight.start(new AlternateTeamFighterOrder());

        turn.perform(move);
        turn.terminate();

        assertEquals(213, fighter.cell().id());
        assertEquals(1, fighter.turn().points().movementPoints());
        assertEquals(6, fighter.turn().points().actionPoints());
    }
}
