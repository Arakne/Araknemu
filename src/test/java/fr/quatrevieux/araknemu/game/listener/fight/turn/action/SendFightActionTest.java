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

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionStarted;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.FightPathValidator;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FightAction;
import fr.quatrevieux.araknemu.network.game.out.fight.action.StartFightAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
            player.fighter(),
            new Path<>(
                fight.map().decoder(),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            ),
            new FightPathValidator[0]
        );

        ActionResult result = move.start();

        listener.on(new FightActionStarted(move, result));

        requestStack.assertAll(new StartFightAction(move), new FightAction(result));
    }

    @Test
    void onActionStartedShouldBeSentToOtherFighter() {
        Move move = new Move(
            other.fighter(),
            new Path<>(
                fight.map().decoder(),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            ),
            new FightPathValidator[0]
        );

        ActionResult result = move.start();

        listener.on(new FightActionStarted(move, result));

        requestStack.assertAll(new FightAction(result));
    }

    @Test
    void whenSecretShouldNotBeSendToOtherFighter() {
        other.fighter().setHidden(other.fighter(), true);
        requestStack.clear();

        Move move = new Move(
            other.fighter(),
            new Path<>(
                fight.map().decoder(),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            ),
            new FightPathValidator[0]
        );

        ActionResult result = move.start();

        listener.on(new FightActionStarted(move, result));
        requestStack.assertEmpty();
    }

    @Test
    void whenSecretShouldBeSendToCurrentFighter() {
        other.fighter().setHidden(player.fighter(), true);
        requestStack.clear();

        Move move = new Move(
            player.fighter(),
            new Path<>(
                fight.map().decoder(),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            ),
            new FightPathValidator[0]
        );

        ActionResult result = move.start();

        listener.on(new FightActionStarted(move, result));
        requestStack.assertAll(new StartFightAction(move), new FightAction(result));
    }

    @Test
    void notSecretHiddenShouldSendPosition() {
        player.fighter().setHidden(player.fighter(), true);
        requestStack.clear();

        Action cast = fight.actions().cast().create(player.fighter(), player.fighter().spells().get(3), other.fighter().cell());

        ActionResult result = cast.start();

        listener.on(new FightActionStarted(cast, result));
        requestStack.assertAll(
            new StartFightAction(cast),
            new FighterPositions(Collections.singleton(player.fighter())),
            new FightAction(result)
        );
    }

    @Test
    void onActionFailed() {
        Move move = new Move(
            player.fighter(),
            new Path<>(
                fight.map().decoder(),
                new ArrayList<>()
            ),
            new FightPathValidator[0]
        );

        ActionResult result = Mockito.mock(ActionResult.class);
        Mockito.when(result.success()).thenReturn(false);
        Mockito.when(result.action()).thenReturn(3);
        Mockito.when(result.performer()).thenReturn(player.fighter());
        Mockito.when(result.arguments()).thenReturn(new Object[0]);

        listener.on(new FightActionStarted(move, result));

        requestStack.assertAll(new FightAction(result));
    }

    @Test
    void whenSecretAndMonsterFighterShouldSendNothing() throws Exception {
        fight = createPvmFight();
        listener = new SendFightAction(fight);

        PlayableFighter monster = (PlayableFighter) new ArrayList<>(fight.team(1).fighters()).get(0);

        monster.setHidden(monster, true);
        requestStack.clear();

        Move move = new Move(
            monster,
            new Path<>(
                fight.map().decoder(),
                Arrays.asList(
                    new PathStep<>(fight.map().get(125), Direction.EAST),
                    new PathStep<>(fight.map().get(139), Direction.SOUTH_WEST)
                )
            ),
            new FightPathValidator[0]
        );

        ActionResult result = move.start();

        listener.on(new FightActionStarted(move, result));
        requestStack.assertEmpty();
    }
}
