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

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.FightPathValidator;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerminateTurnActionTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private TerminateTurnAction handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        handler = new TerminateTurnAction();
        fighter = player.fighter();

        other.fighter().move(fight.map().get(123));
        fighter.move(fight.map().get(185));
    }

    @RepeatedIfExceptionsTest
    void terminateMove() {
        FightTurn turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        turn.perform(
            new Move(
                turn, fighter,
                new Path<>(
                    new Decoder<>(fight.map()),
                    Arrays.asList(
                        new PathStep<>(fight.map().get(185), Direction.EAST),
                        new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                        new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                        new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                    )
                ),
                new FightPathValidator[0]
            )
        );

        requestStack.clear();

        handler.handle(session, new GameActionAcknowledge(0));

        requestStack.assertAll(
            ActionEffect.usedMovementPoints(fighter, 3),
            "GAF2|1"
        );
        assertEquals(198, fighter.cell().id());
        assertEquals(0, turn.points().movementPoints());
    }
}
