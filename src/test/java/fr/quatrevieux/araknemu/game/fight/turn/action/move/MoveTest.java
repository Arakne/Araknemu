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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.move;

import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest extends FightBaseCase {
    private Fight fight;
    private FightTurn turn;
    private Fighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        fighter.move(fight.map().get(185));
        turn = new FightTurn(player.fighter(), fight, Duration.ofSeconds(30));
        turn.start();
    }

    @Test
    void validateEmptyPath() {
        assertFalse(
            new Move(turn, turn.fighter(),
                new Path<>(
                    new Decoder<>(fight.map()),
                    Arrays.asList(new PathStep<>(fight.map().get(185), Direction.EAST))
                )
            ).validate()
        );
    }

    @Test
    void validateNotEnoughMovementPoints() {
        assertFalse(
            new Move(turn, turn.fighter(),
                new Path<>(
                    new Decoder<>(fight.map()),
                    Arrays.asList(
                        new PathStep<>(fight.map().get(185), Direction.EAST),
                        new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                        new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                        new PathStep<>(fight.map().get(227), Direction.SOUTH_WEST),
                        new PathStep<>(fight.map().get(241), Direction.SOUTH_WEST)
                    )
                )
            ).validate()
        );
    }

    @Test
    void validateRestrictedDirection() {
        assertFalse(
            new Move(turn, turn.fighter(),
                new Path<>(
                    new Decoder<>(fight.map()),
                    Arrays.asList(
                        new PathStep<>(fight.map().get(185), Direction.EAST),
                        new PathStep<>(fight.map().get(186), Direction.EAST),
                        new PathStep<>(fight.map().get(187), Direction.EAST),
                        new PathStep<>(fight.map().get(188), Direction.EAST)
                    )
                )
            ).validate()
        );
    }

    @Test
    void validateNotWalkableCells() {
        assertFalse(
            new Move(turn, turn.fighter(),
                new Path<>(
                    new Decoder<>(fight.map()),
                    Arrays.asList(
                        new PathStep<>(fight.map().get(0), Direction.EAST),
                        new PathStep<>(fight.map().get(14), Direction.SOUTH_WEST)
                    )
                )
            ).validate()
        );
    }

    @Test
    void validateValid() {
        assertTrue(
            new Move(turn, turn.fighter(),
                new Path<>(
                    new Decoder<>(fight.map()),
                    Arrays.asList(
                        new PathStep<>(fight.map().get(185), Direction.EAST),
                        new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                        new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                        new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                    )
                )
            ).validate()
        );
    }

    @Test
    void startSuccess() {
        Move move = new Move(turn, turn.fighter(),
            new Path<>(
                new Decoder<>(fight.map()),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            )
        );

        ActionResult result = move.start();

        assertInstanceOf(MoveSuccess.class, result);
        assertEquals(3, MoveSuccess.class.cast(result).movementPointCost());
        assertEquals(198, MoveSuccess.class.cast(result).target().id());

        assertTrue(result.success());
        assertSame(fighter, result.performer());
        assertSame(fighter, result.performer());
        assertEquals(1, result.action());
        assertArrayEquals(new String[] {"ac5ddvfdg"}, result.arguments());
        assertEquals("Move{size=3, target=198}", move.toString());
    }

    @Test
    void end() {
        Move move = new Move(turn, turn.fighter(),
            new Path<>(
                new Decoder<>(fight.map()),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST)
                )
            )
        );

        move.start();
        move.end();

        assertEquals(1, turn.points().movementPoints());
        assertEquals(213, fighter.cell().id());
        assertEquals(Direction.SOUTH_WEST, fighter.orientation());
    }

    @Test
    void duration() {
        Move move = new Move(turn, turn.fighter(),
            new Path<>(
                new Decoder<>(fight.map()),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST)
                )
            )
        );

        assertEquals(Duration.ofMillis(900), move.duration());
    }
}
