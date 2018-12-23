package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ValidateOverweightTest extends GameBaseCase {
    private ValidateOverweight validator;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushItemTemplates()
        ;

        map = explorationPlayer().map();

        validator = new ValidateOverweight();
    }

    @Test
    void validateWithOverweight() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();
        player.inventory().add(container.get(ItemService.class).create(39), 1000);

        Move move = new Move(
            player,
            new Path<>(
                new Decoder<>(player.map()),
                Arrays.asList(
                    new PathStep(map.get(279), Direction.WEST),
                    new PathStep(map.get(278), Direction.WEST)
                )
            ),
            new PathValidator[] {new ValidateOverweight()}
        );

        try {
            validator.validate(move, move.path());

            fail("Except PathValidationException");
        } catch (PathValidationException e) {
            assertEquals(Error.cantMoveOverweight().toString(), e.errorPacket().get().toString());
        }
    }

    @Test
    void validateSuccess() throws SQLException, ContainerException, PathValidationException {
        ExplorationPlayer player = explorationPlayer();

        Path<ExplorationMapCell> path = new Path<>(
            new Decoder<>(player.map()),
            Arrays.asList(
                new PathStep(map.get(279), Direction.WEST),
                new PathStep(map.get(278), Direction.WEST)
            )
        );

        Move move = new Move(player, path, new PathValidator[] {new ValidateOverweight()});

        assertSame(path, validator.validate(move, move.path()));
    }
}
