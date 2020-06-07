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

package fr.quatrevieux.araknemu.game.listener.fight.turn.action;

import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionStarted;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FightAction;
import fr.quatrevieux.araknemu.network.game.out.fight.action.StartFightAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SendFightActionTest extends FightBaseCase {
    private Fight fight;
    private SendFightAction listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendFightAction(
            fight = createFight()
        );

        requestStack.clear();
    }

    @Test
    void onActionStarted() {
        Move move = new Move(
            new FightTurn(player.fighter(), fight, Duration.ofSeconds(30)),
            player.fighter(),
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

        listener.on(new FightActionStarted(move, result));

        requestStack.assertAll(new StartFightAction(move), new FightAction(result));
    }

    @Test
    void onActionFailed() {
        Move move = new Move(
            new FightTurn(player.fighter(), fight, Duration.ofSeconds(30)),
            player.fighter(),
            new Path<>(
                new Decoder<>(fight.map()),
                new ArrayList<>()
            )
        );

        ActionResult result = Mockito.mock(ActionResult.class);
        Mockito.when(result.success()).thenReturn(false);
        Mockito.when(result.action()).thenReturn(3);
        Mockito.when(result.performer()).thenReturn(player.fighter());
        Mockito.when(result.arguments()).thenReturn(new Object[0]);

        listener.on(new FightActionStarted(move, result));

        requestStack.assertAll(new FightAction(result));
    }
}
