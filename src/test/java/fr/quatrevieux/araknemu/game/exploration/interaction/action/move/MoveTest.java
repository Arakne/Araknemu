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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.move;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.*;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathException;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest extends GameBaseCase {
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        player = explorationPlayer();
    }

    @Test
    void success() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
            new PathValidator[] {new ValidateWalkable()}
        );

        move.setId(1);

        player.interactions().push(move);

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.MOVE, gamePlayer().id(), "aexbftdgl")
        );

        assertTrue(player.interactions().busy());

        player.interactions().end(1);
        assertFalse(player.interactions().busy());

        assertEquals(395, player.position().cell());
        assertEquals(Direction.SOUTH_WEST, player.orientation());
    }

    @Test
    void data() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
            new PathValidator[] {new ValidateWalkable()}
        );

        move.setId(1);

        player.interactions().push(move);

        assertEquals(1, move.id());
        assertEquals(player, move.performer());
        assertArrayEquals(new Object[] {"aexbftdgl"}, move.arguments());
        assertEquals(ActionType.MOVE, move.type());
    }

    @Test
    void invalidPath() {
        Move move = new Move(
            player,
            new Path<>(new Decoder<>(player.map())),
            new PathValidator[] {new ValidateWalkable()}
        );

        move.setId(1);

        assertThrows(Exception.class, () -> player.interactions().push(move), "Empty path");
        assertFalse(player.interactions().busy());
    }

    @Test
    void moveWithCancel() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
            new PathValidator[] {new ValidateWalkable()}
        );

        move.setId(1);

        player.interactions().push(move);

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.MOVE, gamePlayer().id(), "aexbftdgl")
        );

        assertTrue(player.interactions().busy());

        player.interactions().cancel(1, "294");
        assertFalse(player.interactions().busy());

        assertEquals(294, player.position().cell());
        assertEquals(Direction.SOUTH_EAST, player.orientation());
    }

    @Test
    void moveWithCancelNull() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
            new PathValidator[] {new ValidateWalkable()}
        );

        move.setId(1);

        player.interactions().push(move);

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.MOVE, gamePlayer().id(), "aexbftdgl")
        );

        assertTrue(player.interactions().busy());

        player.interactions().cancel(1, null);
        assertFalse(player.interactions().busy());

        assertEquals(279, player.position().cell());
    }

    @Test
    void moveWithCancelNotInPath() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
            new PathValidator[] {new ValidateWalkable()}
        );

        move.setId(1);

        player.interactions().push(move);

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.MOVE, gamePlayer().id(), "aexbftdgl")
        );

        assertTrue(player.interactions().busy());

        assertThrows(Exception.class, () -> player.interactions().cancel(1, "410"), "Invalid cell");
        assertFalse(player.interactions().busy());

        assertEquals(279, player.position().cell());
    }

    @Test
    void blockedPath() throws PathException, ContainerException {
        player.join(container.get(ExplorationMapService.class).load(10340));
        player.move(player.map().get(169), Direction.SOUTH_EAST);
        requestStack.clear();

        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("acPfcl", player.map().get(169)),
            new PathValidator[] {new ValidateWalkable()}
        );

        player.interactions().push(move);

        assertFalse(player.interactions().busy());
        requestStack.assertLast(GameActionResponse.NOOP);
    }

    @Test
    void pathValidationExceptionWithErrorPacket() throws PathException, ContainerException {
        player.join(container.get(ExplorationMapService.class).load(10340));
        player.move(player.map().get(169), Direction.SOUTH_EAST);
        requestStack.clear();

        PathValidator validator = (a, p) -> { throw new PathValidationException("my error"); };

        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("acPfcl", player.map().get(169)),
            new PathValidator[] {validator}
        );

        player.interactions().push(move);

        assertFalse(player.interactions().busy());
        requestStack.assertAll(GameActionResponse.NOOP, "my error");
    }

    @Test
    void pathValidationExceptionWithoutErrorPacket() throws PathException, ContainerException {
        player.join(container.get(ExplorationMapService.class).load(10340));
        player.move(player.map().get(169), Direction.SOUTH_EAST);
        requestStack.clear();

        PathValidator validator = (a, p) -> { throw new PathValidationException(null); };

        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("acPfcl", player.map().get(169)),
            new PathValidator[] {validator}
        );

        player.interactions().push(move);

        assertFalse(player.interactions().busy());
        requestStack.assertAll(GameActionResponse.NOOP);
    }

    @Test
    void functionalSuccess() throws Exception {
        handlePacket(new GameActionRequest(ActionType.MOVE.id(), new String[] {"bftdgl"}));

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.MOVE, player.id(), "aexbftdgl")
        );

        assertTrue(player.interactions().busy());
    }

    @Test
    void functionalOverweight() throws Exception {
        dataSet.pushItemTemplates();

        player.inventory().add(container.get(ItemService.class).create(39), 1000);
        requestStack.clear();
        handlePacket(new GameActionRequest(ActionType.MOVE.id(), new String[] {"bftdgl"}));

        assertFalse(player.interactions().busy());
        requestStack.assertAll(GameActionResponse.NOOP, Error.cantMoveOverweight());
    }

    @Test
    void functionalSuccessWithRestrictedDirections() throws Exception {
        player.player().restrictions().unset(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);
        requestStack.clear();

        handlePacket(new GameActionRequest(ActionType.MOVE.id(), new String[] {"bftdgl"}));

        requestStack.assertLast(new GameActionResponse("1", ActionType.MOVE, player.id(), "aexbftdgl"));
        assertTrue(player.interactions().busy());
    }

    @Test
    void functionalErrorWithRestrictedDirections() throws Exception {
        player.player().restrictions().unset(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);
        requestStack.clear();

        handlePacket(new GameActionRequest(ActionType.MOVE.id(), new String[] {"aey"}));

        requestStack.assertLast(GameActionResponse.NOOP);
    }
}
